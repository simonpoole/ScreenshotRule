package ch.poole.android.screenshotrule;

import static android.graphics.Bitmap.Config.ARGB_8888;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Looper;
import android.view.View;
import androidx.annotation.NonNull;

/**
 * Derived from Apache 2.0 licensed code by Square, Inc.
 * 
 * Instead of just rendering the activities decorview this uses reflection to try to render all windows in their correct
 * position, something much nearer to an actual screenshot
 * 
 * Original see
 * https://github.com/square/spoon/blob/master/spoon-client/src/main/java/com/squareup/spoon/Screenshot.java
 *
 */
final class Screenshot {

    /**
     * 
     * 
     * @param tag
     * @param activity
     * @return
     */
    static Bitmap capture(@NonNull String tag, @NonNull Activity activity) {
        return drawCanvas(tag, activity);
    }

    private static Bitmap drawCanvas(@NonNull String tag, @NonNull final Activity activity) {
        View view = activity.getWindow().getDecorView();
        if (view.getWidth() == 0 || view.getHeight() == 0) {
            throw new IllegalStateException(
                    "Your view has no height or width. Are you sure " + activity.getClass().getSimpleName() + " is the currently displayed activity?");
        }
        final Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), ARGB_8888);

        if (Looper.myLooper() == Looper.getMainLooper()) {
            // On main thread already, Just Do It™.
            drawViewsToBitmap(activity, bitmap);
        } else {
            // On a background thread, post to main.
            final CountDownLatch latch = new CountDownLatch(1);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        drawViewsToBitmap(activity, bitmap);
                    } finally {
                        latch.countDown();
                    }
                }
            });
            try {
                latch.await();
            } catch (InterruptedException e) {
                throw new RuntimeException("Unable to get screenshot '" + tag + "'", e);
            }
        }
        return bitmap;
    }

    private static void drawViewsToBitmap(Activity activity, Bitmap bitmap) {
        Canvas canvas = new Canvas(bitmap);
        int left = 0;
        int top = 0;
        for (View v : getWindowManagerViews()) {
            canvas.save();
            left += v.getLeft();
            top += v.getTop();
            canvas.translate(left, top);
            v.draw(canvas);
            canvas.restore();
        }
    }

    /**
     * Get a list of the windows managed by the window manager
     * 
     * See
     * https://stackoverflow.com/questions/19669984/is-there-a-way-to-programmatically-locate-all-windows-within-a-given-application
     * 
     * @return a List of Views
     */
    private static List<View> getWindowManagerViews() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH && Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                // get the list from WindowManagerImpl.mViews
                Class wmiClass = Class.forName("android.view.WindowManagerImpl");
                Object wmiInstance = wmiClass.getMethod("getDefault").invoke(null);
                return viewsFromWM(wmiClass, wmiInstance);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                // get the list from WindowManagerGlobal.mViews
                Class wmgClass = Class.forName("android.view.WindowManagerGlobal");
                Object wmgInstance = wmgClass.getMethod("getInstance").invoke(null);
                return viewsFromWM(wmgClass, wmgInstance);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ArrayList<View>();
    }

    /**
     * See
     * https://stackoverflow.com/questions/19669984/is-there-a-way-to-programmatically-locate-all-windows-within-a-given-application
     * 
     * @param wmClass the WindowManager class
     * @param wmInstance the WindowManager instance
     * @return a List of Views
     * @throws Exception if something goes wrong
     */
    private static List<View> viewsFromWM(@NonNull Class wmClass, @NonNull Object wmInstance) throws Exception {
        Field viewsField = wmClass.getDeclaredField("mViews");
        viewsField.setAccessible(true); // NOSONAR
        Object views = viewsField.get(wmInstance);
        if (views instanceof List) {
            return (List<View>) viewsField.get(wmInstance);
        } else if (views instanceof View[]) {
            return Arrays.asList((View[]) viewsField.get(wmInstance));
        }

        return new ArrayList<View>();
    }

}

# ScreenshotRule

Junit rule to generate screenshots when running tests with Spoon.

This replaces the Spoon "SpoonRule.screenshot". As the Spoon implementation only renders the top level "DecorView" it will not render any dialogs that create additional "Windows", while relying on reflection and due to that by definition a hack, this modified library does provide something closer to an actual screenshot.

The original code can be found here https://github.com/square/spoon/tree/master/spoon-client

## Usage

``` java
    @Rule
    public ScreenshotRule screenshotRule = new ScreenshotRule();
    ...
    screenshotRule.screenshot(activity, "tag");
```


## Including in your project

Add the following to your *build.gradle* file(s):

``` groovy
repositories {
    maven {
        // jcenter() not published yet
        maven { url 'https://dl.bintray.com/content/simonpoole/android' }
    }
}
```

``` groovy
dependencies {
    androidTestImplementation "com.squareup.spoon:spoon-client:2.0.0-SNAPSHOT"
    androidTestImplementation 'ch.poole.android:ScreenshotRule:0.0.0'
}
```

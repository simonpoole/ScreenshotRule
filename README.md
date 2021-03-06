# ScreenshotRule

Junit rule to generate screenshots when running tests with Spoon.

This replaces the Spoon "SpoonRule.screenshot". As the Spoon implementation only renders the top level "DecorView" it will not render any dialogs that create additional "Windows", while relying on reflection and due to that by definition a hack, this modified library does provide something closer to an actual screenshot. Using the hack can be avoided by using UiAutomation.takeScreenshot() with saveScreenshot.

The original code can be found here https://github.com/square/spoon/tree/master/spoon-client

## Usage

``` java
    @Rule
    public ScreenshotRule screenshotRule = new ScreenshotRule();
    ...
    screenshotRule.screenshot(activity, "tag");
```

or

``` java
    @Rule
    public ScreenshotRule screenshotRule = new ScreenshotRule();
    ...
    screenshotRule.saveScreenshot(activity, "tag", instrumentation.getUiAutomation().takeScreenshot());
```


## Including in your project

Add the following to your *build.gradle* file(s):

``` groovy
repositories {
    maven {
        mavenCentral()
    }
}
```

``` groovy
dependencies {
    androidTestImplementation "com.squareup.spoon:spoon-client:2.0.0-SNAPSHOT"
    androidTestImplementation 'ch.poole.android:ScreenshotRule:0.2.0'
}
```

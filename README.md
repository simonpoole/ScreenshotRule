# ScreenshotRule

Junit rule to get screenshots when running tests with Spoon.

This replaces the Spoon "SpoonRule.screenshot" with something that actually emulates a screenshot. As the Spoon implementation only renders the top level "DecorView" it will not render any dialogs that create additional "Windows", while relying on reflection and due to that by definition a hack, this modified library does provide something near to an actual screenshot.

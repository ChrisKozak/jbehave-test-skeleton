package com.thoughtworks.core.utils;

import com.thoughtworks.core.web.Browser;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import static com.thoughtworks.core.utils.CoreUtils.getLoggerFor;

public class ScreenshotAssertionListener extends AssertionListener {
    private Browser browser;

    public ScreenshotAssertionListener(Browser browser) {
        this.browser = browser;
    }

    @Override
    public void afterAssertFailed() {
        resetUUID();
        extractScreenshotIfPossible();
    }

    private void extractScreenshotIfPossible() {
        File screenShotTempFile = browser.takeScreenshot();
        if (screenShotTempFile != null) {
            File screenshotFile = getUniqueFile(currentUUID(), "png");
            moveTempFile(screenShotTempFile, screenshotFile);
            getLoggerFor(this).info("Screenshot file name: " + screenshotFile.getAbsolutePath());
        }
    }



    @Override
    public String afterStepFailed(AssertionLog assertionLog, String storyName, String step, Throwable failure) {
        if (!getUniqueFile(currentUUID(), "png").exists()){
            afterAssertFailed();
        }

        String href = "../resources/" + storyName + currentUUID() + ".png";
        return "<div class=\"screenshotHolder\"><p><a href=\"" + href + "\"><img class=\"screenshot\" src=\"" + href + "\"/></a></p></div>\n";
    }

    @Override
    public Integer getIndex() {
        return 100;
    }

    /**
     * Copy and delete to handle case where source and target are on different
     * file systems.
     */
    private void moveTempFile(File screenShotTempFile, File screenshotFile) {
        try {
            FileUtils.copyFile(screenShotTempFile, screenshotFile, true);
        } catch (IOException e) {
            getLoggerFor(this).error("Could not rename file '" + screenShotTempFile + "' to '" + screenshotFile + "'", e);
        }
        if (!screenShotTempFile.delete()) {
            getLoggerFor(this).warn("Could not delete screen shot temp file: '" + screenShotTempFile + "'");
        }
    }
}

package com.thoughtworks.core.utils;

import com.thoughtworks.core.web.Browser;
import org.apache.log4j.Logger;

import java.io.File;

import static com.thoughtworks.core.utils.CoreUtils.getLoggerFor;
import static org.apache.commons.io.FileUtils.writeStringToFile;

public class HtmlSourceAssertionListener extends AssertionListener {
    private Browser browser;
    private Logger logger = CoreUtils.getLoggerFor(this);

    public HtmlSourceAssertionListener(Browser browser) {
        this.browser = browser;
    }

    @Override
    public void afterAssertFailed() {
        resetUUID();
        extractPageSource();
    }

    private void extractPageSource() {
        File file = AssertionListener.getUniqueFile(currentUUID(), "htm");
        try {
            writeStringToFile(file, browser.getPageSource());
            logger.info("HTML source file: " + file.getAbsolutePath());
        } catch (Exception e) {
            logger.error("Can't extract page source because of the exception below", e);
        }
    }

    @Override
    public String afterStepFailed(AssertionLog assertionLog, String storyName, String step, Throwable failure) {
        if (!AssertionListener.getUniqueFile(currentUUID(), "htm").exists()){
            afterAssertFailed();
        }
        return "<div><a target=\"_blank\" href=\"../resources/" + storyName + currentUUID() + ".htm\">View page source</a></div>\n";
    }

    @Override
    public Integer getIndex() {
        return 50;
    }
}

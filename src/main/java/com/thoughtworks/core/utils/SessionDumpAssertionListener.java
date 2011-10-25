package com.thoughtworks.core.utils;

import com.thoughtworks.core.web.Browser;
import org.apache.log4j.Logger;

import java.io.File;

import static com.thoughtworks.core.utils.CoreUtils.getLoggerFor;
import static org.apache.commons.io.FileUtils.writeStringToFile;

public class SessionDumpAssertionListener extends AssertionListener {
    private Logger logger = getLoggerFor(this);
    private GlobalClipboard globalClipboard;
    private Browser browser;

    public SessionDumpAssertionListener(GlobalClipboard globalClipboard, Browser browser) {
        this.globalClipboard = globalClipboard;
        this.browser = browser;
    }

    @Override
    public void afterAssertFailed() {
        createSessionDumpFile();
    }

    @Override
    public String afterScenario(String storyName) {
        createSessionDumpFile();
        return createLinkToSessionDump(storyName);
    }

    private void createSessionDumpFile() {
        try {
            File file = getUniqueFile(resetUUID(), "txt");
            logger.info("Session Dump file: " + file.getAbsolutePath());
            writeStringToFile(file, globalClipboard.toString());
        } catch (Exception e) {
            logger.error("Session information failed to write to disk! ", e);
        }
    }

    @Override
    public String afterStepFailed(AssertionLog assertionLog, String storyName, String step, Throwable failure) {
        if (!getUniqueFile(resetUUID(), "txt").exists()){
            afterAssertFailed();
        }

        return createLinkToSessionDump(storyName);
    }

    private String createLinkToSessionDump(String storyName) {
        String href = "../resources/" + storyName + currentUUID() + ".txt";
        return "<div><a href='" + href + "'>Session Dump</a></div>";
    }
}

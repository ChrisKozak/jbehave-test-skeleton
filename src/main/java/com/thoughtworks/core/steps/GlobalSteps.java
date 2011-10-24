package com.thoughtworks.core.steps;

import com.thoughtworks.core.domain.ExtendedJarFile;
import com.thoughtworks.core.utils.AssertionLog;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.EmptyFileFilter;
import org.apache.log4j.Logger;
import org.jbehave.core.annotations.AfterScenario;
import org.jbehave.core.annotations.AfterStories;
import org.jbehave.core.annotations.BeforeScenario;
import org.jbehave.core.annotations.BeforeStories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;

import static com.thoughtworks.core.utils.CoreUtils.*;
import static java.io.File.separatorChar;

/**
 * These steps are the global steps that should be reusable by any story.
 */
@Component
public class GlobalSteps {
    private final Logger logger = getLoggerFor(this);
    @Autowired
    private AssertionLog assertionLog;

    @BeforeStories
    public void logStuff(){
        logger.info("The default encoding is " + Charset.defaultCharset());
    }

    @AfterStories
    public void copyStyling() {
        try {
            copyDirectoryToReportDirectoryFrom("reports/style");
            copyDirectoryToReportDirectoryFrom("storynavigator/images");
            copyDirectoryToReportDirectoryFrom("storynavigator/js");
            copyDirectoryToReportDirectoryFrom("storynavigator/style");
            copyFileToReportDirectory("storynavigator/navigator.html");
            copyDirectoryToReportDirectoryFrom("storynavigator/jasmine");
            deleteNonAllowedStoriesFiles();

        } catch (Exception e) {
            logger.error("Error occurred while copying styling", e);
            throw new RuntimeException(e);
        }
    }

    private void deleteNonAllowedStoriesFiles() {
        logger.info("Now removing story reports that were not allowed...");

        for (String fileName : resolveBuildDirectory().list(EmptyFileFilter.EMPTY)) {
            try {
                new File(resolveBuildDirectory().getCanonicalPath() + separatorChar + fileName).delete();
            } catch (IOException ioException) {
                logger.error(fileName + " was unable to be found or deleted");
            }
        }
    }

    private void copyFileToReportDirectory(String fileToBeCopied) throws URISyntaxException, IOException {
        File destination = resolveDirectory("view");
        InputStream fileAsResourceStream = getClass().getClassLoader().getResourceAsStream(fileToBeCopied);
        FileWriter fileWriter = new FileWriter(new File(destination, new File(fileToBeCopied).getName()));
        IOUtils.copy(fileAsResourceStream, fileWriter);
        IOUtils.closeQuietly(fileWriter);
    }

    private void copyDirectoryToReportDirectoryFrom(String directoryToBeExtracted) throws IOException, URISyntaxException {
        File destination = resolveDirectory("view");
        URL directoryAsResource = getClass().getClassLoader().getResource(directoryToBeExtracted);

        if (directoryAsResource.getProtocol().equals("jar")) {
            logger.info("Copying " + directoryToBeExtracted + " to " + destination);
            ExtendedJarFile.fromUrl(directoryAsResource).extract(directoryToBeExtracted, destination);
        } else if (directoryAsResource.getProtocol().equals("file")) {
            logger.info("Copying " + directoryToBeExtracted + " to " + destination);
            FileUtils.copyDirectoryToDirectory(new File(directoryAsResource.toURI()), destination);
        } else {
            logger.error("Report styling files will not be copied. We don't know how to copy styling when URL protocol is " + directoryAsResource.getProtocol());
        }
    }

    @BeforeScenario
    public void clearAssertionLog() {
        assertionLog.clear();
    }

    @AfterScenario
    public void failOnAssertionFailure() {
        if (assertionLog.hasAnyFailures()) {
            throw new ScenarioFailedFromAssertionFailuresException("This scenario failed because of assertion failures.");
        }
    }
}
package com.thoughtworks.core.web;

import com.thoughtworks.core.domain.ExtendedJarFile;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.thoughtworks.core.utils.CoreUtils.getLoggerFor;
import static org.apache.commons.io.FileUtils.deleteDirectory;

public class WebDriverFactory {
    private static final String CLASSLOADER_RESOURCE_SEPARATOR = "/"; // always "/", never "\"
    private static final String CHECKED_IN_PROFILE_ROOT_DIR = "profiles";
    private static final String CHECKED_IN_FIREFOX_PROFILE_NAME = "TestProfile";

    private final Logger logger = getLoggerFor(this);
    private static final Pattern JAR_PATH_PATTERN = Pattern.compile("^.*file:(/.*\\.jar).*$");

    public WebDriver getWebDriver() {
        logger.info("WebDriverFacotry is using PATH: " + System.getenv("PATH"));
        return new FirefoxDriver(getDefaultProfile());
    }

    private FirefoxProfile getDefaultProfile() {
        File firefoxProfileDirectory = getDefaultFirefoxProfileDirectory();
        logger.info("Using Firefox profile at: " + firefoxProfileDirectory);

        FirefoxProfile firefoxProfile = new FirefoxProfile(firefoxProfileDirectory);

        return firefoxProfile;
    }

   private File getDefaultFirefoxProfileDirectory() {
        String profilePath = CHECKED_IN_PROFILE_ROOT_DIR + CLASSLOADER_RESOURCE_SEPARATOR + CHECKED_IN_FIREFOX_PROFILE_NAME;
        URL resource = getClass().getClassLoader().getResource(profilePath);
        if (isProfileFromJar(resource)) {
            return extractProfileFromJar(profilePath, resource);
        } else {
            return useProfileDirectly(resource);
        }
    }

    private File useProfileDirectly(URL resource) {
        try {
            return new File(resource.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isProfileFromJar(URL resource) {
        return ! resource.toExternalForm().contains("/target");
    }

    private boolean extractDirectoryDoesNotExistOrIsOutOfDate(File jarFile, File extractDir) {
        return !extractDir.exists() || jarFile.lastModified() > extractDir.lastModified();
    }

    private File extractProfileFromJar(String profilePath, URL resource) {
        String pathToJar = null;
        try {
            pathToJar = findJarPath(URLDecoder.decode(resource.toExternalForm(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("This should not ever happen, but the encoding parameter was invalid", e);
        }
        File locationOfDefaultProfile = new File("target/");  // assumes current working directory is the project root
        File jarFile = new File(pathToJar);
        File extractDir = new File(locationOfDefaultProfile, CHECKED_IN_FIREFOX_PROFILE_NAME);
        if (extractDirectoryDoesNotExistOrIsOutOfDate(jarFile, extractDir)) {
            try {
                deleteDirectory(extractDir);
            } catch (IOException e) {
                throw new RuntimeException("Can't delete directory" + extractDir, e);
            }
            try {
                new ExtendedJarFile(new JarFile(jarFile)).extract(profilePath, locationOfDefaultProfile);
            } catch (IOException e) {
                throw new RuntimeException("Can't extract jar file" + pathToJar, e);
            }
        }
        return new File(locationOfDefaultProfile, CHECKED_IN_FIREFOX_PROFILE_NAME);
    }

    private static String findJarPath(String urlAsString) {
        Matcher matcher = JAR_PATH_PATTERN.matcher(urlAsString);
        if (!matcher.matches()) {
            throw new IllegalStateException("Can't parse jar name from classpath resource profile was found in. Resource: " + urlAsString);
        }
        return matcher.group(1);
    }
}

package com.thoughtworks.core.web;

import org.openqa.selenium.WebDriverException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class Browser extends AbstractComponent {
    @Autowired
    private TargetUrl siteUrl;

    public void navigateToHomepage() {
        extendedWebDriver.navigate().to(siteUrl.getFullUrl());
    }

    public void restart() {
        extendedWebDriver.simulateBrowserRestart();
    }

    public void back() {
        extendedWebDriver.navigate().back();
    }

    public void refresh() {
        extendedWebDriver.navigate().refresh();
    }

    public String getPageSource() {
        return extendedWebDriver.getPageSource();
    }

    public void clearSession() {
        String url = siteUrl.getFullUrl();
        extendedWebDriver.navigate().to(url);
        deleteAllCookies();
        extendedWebDriver.closeAllButTheMainBrowserWindow();
        extendedWebDriver.navigate().to(url);
    }

    private void deleteAllCookies() {
        try {
            extendedWebDriver.manage().deleteAllCookies();
        } catch (WebDriverException e) {
            ignoreExceptionIfUrlIsBad(e);
        }
    }

    private void ignoreExceptionIfUrlIsBad(WebDriverException e) {
        if (!e.getMessage().startsWith("Component returned failure code")) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        try {
            extendedWebDriver.quit();
        } catch (Exception e) {
            logger.error("Could not close the web client: " + extendedWebDriver);
        }
    }

    public void closeAllButTheMainBrowserWindow() {
        extendedWebDriver.closeAllButTheMainBrowserWindow();
    }

    public boolean textIsVisibleOnPage(String text) {
        return extendedWebDriver.patientlyVerifyTextIsPresent(text);
    }

    public File takeScreenshot() {
        return extendedWebDriver.getScreenshot();
    }

    public void rememberBaseWindowHandle() {
        extendedWebDriver.rememberBaseBrowserWindowHandle();
    }

    public String getCurrentUrl() {
        return extendedWebDriver.getCurrentUrl();
    }

    public int getWindowHandles() {
        return extendedWebDriver.getWindowHandles().size();
    }
}

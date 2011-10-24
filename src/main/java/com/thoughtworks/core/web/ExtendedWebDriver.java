package com.thoughtworks.core.web;

import com.thoughtworks.core.utils.DisplayFriendlyNoSuchElementException;
import com.thoughtworks.core.utils.ElementNotFound;
import com.thoughtworks.core.utils.ExtendedWebElement;
import com.thoughtworks.core.utils.ToExtendedWebElement;
import com.google.common.base.Predicate;
import org.apache.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static ch.lambdaj.Lambda.*;
import static com.thoughtworks.core.utils.CoreUtils.getLoggerFor;
import static com.thoughtworks.core.utils.DisplayFriendlyNoSuchElementException.getErrorMessage;
import static com.google.common.collect.Iterables.all;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.transform;
import static java.lang.System.currentTimeMillis;
import static java.lang.Thread.sleep;
import static org.hamcrest.Matchers.is;
import static org.openqa.selenium.OutputType.FILE;

public class ExtendedWebDriver implements WebDriver {
    static final String NEEDS_A_NAME = "Needs a name";
    private WebDriver webDriver;
    private WebDriverWait wait;
    private String baseBrowserWindowHandle;
    private final Logger logger = getLoggerFor(this);

    private static final long DEFAULT_MAX_WAIT = 15000;

    @Autowired
    public ExtendedWebDriver(WebDriver webDriver, WebDriverWait wait) {
        this.webDriver = webDriver;
        this.wait = wait;
    }

    @Override
    public void get(String s) {
        webDriver.get(s);
    }

    @Override
    public String getCurrentUrl() {
        return webDriver.getCurrentUrl();
    }

    @Override
    public String getTitle() {
        return webDriver.getTitle();
    }

    @Override
    @Deprecated
    public List<WebElement> findElements(By by) {
        return webDriver.findElements(by);
    }

    public List<ExtendedWebElement> findElements(String elementDisplayName, By by) {
        List<WebElement> elements = webDriver.findElements(by);
        if (elements.isEmpty()) {
            logger.info(getErrorMessage(elementDisplayName, by));
        }

        return transform(elements, new ToExtendedWebElement(elementDisplayName, this));
    }

    @Override
    @Deprecated
    public ExtendedWebElement findElement(By by) {
        return new ExtendedWebElement("", webDriver.findElement(by), this);
    }

    public ExtendedWebElement findElement(String elementDisplayName, By... bys) {
        Throwable lastException = null;
        for (By by : bys) {
            try {
                return new ExtendedWebElement(elementDisplayName, webDriver.findElement(by), this);
            } catch (NoSuchElementException x) {
                lastException = x;
            }
        }
        throw new DisplayFriendlyNoSuchElementException(lastException, elementDisplayName, bys);
    }

    @Override
    public String getPageSource() {
        return webDriver.getPageSource();
    }

    @Override
    public void close() {
        webDriver.close();
    }

    @Override
    public void quit() {
        webDriver.quit();
    }

    @Override
    public Set<String> getWindowHandles() {
        return webDriver.getWindowHandles();
    }

    @Override
    public String getWindowHandle() {
        return webDriver.getWindowHandle();
    }

    @Override
    public TargetLocator switchTo() {
        return webDriver.switchTo();
    }

    @Override
    public Navigation navigate() {
        return webDriver.navigate();
    }

    public void goTo(String url) {
        webDriver.navigate().to(url);
    }

    @Override
    public Options manage() {
        return webDriver.manage();
    }

    /**
     * @throws DisplayFriendlyNoSuchElementException
     *          if we reached the last By identifier without finding any web element
     * @returns the first element that could be found using each By in turn
     */
    public ExtendedWebElement patientlyFindElement(String elementDisplayName, By... bys) {
        patientlyVerifyAnyElementIsPresent(elementDisplayName, bys);
        Throwable lastException = null;
        for (By by : bys) {
            try {
                return getExtendedWebElementWhenMatchingElementIsActive(elementDisplayName, by);
            } catch (Exception x) {
                lastException = x;
            }
        }
        throw new DisplayFriendlyNoSuchElementException(lastException, elementDisplayName, bys);
    }

    @Deprecated
    public ExtendedWebElement patientlyFindElement(By elementIdentifier) {
        patientlyVerifyAnyElementIsPresent(NEEDS_A_NAME, elementIdentifier);
        try {
            return getExtendedWebElementWhenMatchingElementIsActive("", elementIdentifier);
        } catch (NoSuchElementException x) {
            throw new DisplayFriendlyNoSuchElementException("Missing a name", elementIdentifier);
        }
    }

    /**
     * @param elementDisplayName a user-friendly name to describe what you are looking for
     * @param anchor             an element that you want to find before you get to what you're really interested in
     * @param allBys             the By for the element you are looking for once the anchor element was found
     */
    public ExtendedWebElement findElementOnceAnchorIsDisplayed(String elementDisplayName, By anchor, By... allBys) {
        List<ExtendedWebElement> elements = findElementsOnceAnchorIsDisplayed(elementDisplayName, anchor, allBys);
        if (elements.size() == 0) {
            throw new DisplayFriendlyNoSuchElementException(elementDisplayName, allBys);
        }
        return elements.get(0);
    }

    /**
     * @param elementDisplayName a user-friendly name to describe what you are looking for
     * @param anchor             an element that you want to find before you get to what you're really interested in
     * @param allBys             the By for the element you are looking for once the anchor element was found
     */
    public List<ExtendedWebElement> findElementsOnceAnchorIsDisplayed(String elementDisplayName, By anchor, By... allBys) {
        patientlyFindElement("Anchor for " + elementDisplayName, anchor);
        List<ExtendedWebElement> all = new ArrayList<ExtendedWebElement>();
        for (By by : allBys) {
            all.addAll(findElements(elementDisplayName, by));
        }
        return all;
    }

    /**
     * You should now use #findElementsOnceAnchorIsDisplayed to make your tests faster
     */
    @Deprecated
    public List<ExtendedWebElement> patientlyFindElements(String elementDisplayName, By... allBys) {
        List<ExtendedWebElement> all = new ArrayList<ExtendedWebElement>();
        for (By by : allBys) {
            all.addAll(getAllActiveWebElements(elementDisplayName, by));
        }

        if (all.isEmpty()) {
            logger.debug(getErrorMessage(elementDisplayName, allBys));
        }
        return all;
    }

    @Deprecated
    public List<ExtendedWebElement> patientlyFindElements(By by) {
        List<ExtendedWebElement> allActiveWebElements = getAllActiveWebElements(NEEDS_A_NAME, by);
        if (allActiveWebElements.isEmpty()) {
            logger.debug(getErrorMessage(NEEDS_A_NAME, by));
        }
        return allActiveWebElements;
    }

    private ExtendedWebElement getExtendedWebElementWhenMatchingElementIsActive(String elementDisplayName, By by) {
        ExtendedWebElement extendedWebElement = new ExtendedWebElement(elementDisplayName, webDriver.findElement(by), this);
        wait.until(new ElementIsActive(elementDisplayName, by));
        return extendedWebElement;
    }

    private List<ExtendedWebElement> getAllActiveWebElements(String elementDisplayName, By by) {
        try {
            wait.until(new ElementsAreActive(elementDisplayName, by));
            return findElements(elementDisplayName, by);
        } catch (TimeoutException te) {
            return select(findElements(elementDisplayName, by), having(on(WebElement.class).isEnabled(), is(true)));
        }
    }

    public boolean patientlyVerifyTextIsPresent(String string) {
        return patientlyVerify(new TextIsFound(string));
    }

    public boolean patientlyVerifyAnyTextIsPresent(By anchor, String... strings) {
        patientlyFindElement("Anchor for " + join(strings), anchor);
        for (String string : strings) {
            if (verifyTextIsPresent(string)) {
                return true;
            }
        }
        return false;
    }

    public void waitForABitThenIgnoreIfNotFound(String elementDisplayName, By by) {
        try {
            new WebDriverWait(webDriver, 10).until(new ElementIsActive(elementDisplayName, by));
        } catch (TimeoutException e) {
            logger.info(elementDisplayName + " never appeared. Assuming rest of page loaded.");
        }
    }

    public ExtendedWebElement quietlyFindElement(By by) {
        try {
            return findElement("quietly finding an element", by);
        } catch (Exception e) {
            return null;
        }
    }

    public Alert getAlertOrNull() {     // Used to verify the negative case ... when an alert is not expected.
        try {
            return switchTo().alert(); // this will patiently wait for an alert. The patient wait is needed. See http://code.google.com/p/selenium/issues/detail?id=2438
        } catch (NoAlertPresentException e) {
            // there should not be an alert
        }
        return null;
    }

    private class TextIsFound implements ExpectedCondition<Boolean> {
        private String text;

        public TextIsFound(String text) {
            this.text = text;
        }

        @Override
        public Boolean apply(WebDriver webDriver) {
            return verifyTextIsPresent(text);
        }
    }

    public boolean verifyTextIsPresent(String string) {
        return patientlyFindElement("HTML content", By.tagName("html")).getText().contains(string);
    }

    public boolean patientlyVerify(ExpectedCondition condition) {
        try {
            wait.until(condition);
            return true;
        } catch (Exception e) {
            logger.debug("Patiently ignoring the following error: " + e.getMessage());
            return false;
        }
    }

    private void patientlyVerifyAnyElementIsPresent(String elementDisplayName, By... elementIdentifiers) {
        long cutOffTime = currentTimeMillis() + DEFAULT_MAX_WAIT;
        while (all(newArrayList(elementIdentifiers), new NotFound())) {
            waitASecond();
            if (cutOffTime < currentTimeMillis()) {
                throw new DisplayFriendlyNoSuchElementException(elementDisplayName, elementIdentifiers);
            }
        }
    }

    @Deprecated
    public boolean isElementEventuallyDisplayed(By by) {
        try {
            return patientlyFindElement(by).isDisplayed();
        } catch (DisplayFriendlyNoSuchElementException e) {
            return false;
        } catch (NoSuchElementException e) {
            return false;
        } catch (TimeoutException e) {
            return false;
        }
    }

    /**
     * You should now use overloaded @see #isElementEventuallyDisplayed(String, By, By) to make your test run faster
     */
    @Deprecated
    public boolean isElementEventuallyDisplayed(String elementDisplayName, By by) {
        try {
            return patientlyFindElement(elementDisplayName, by).isDisplayed();
        } catch (DisplayFriendlyNoSuchElementException e) {
            logger.debug(getErrorMessage(elementDisplayName, by));
            return false;
        } catch (NoSuchElementException e) {
            logger.debug(getErrorMessage(elementDisplayName, by));
            return false;
        } catch (TimeoutException e) {
            logger.debug(getErrorMessage(elementDisplayName, by));
            return false;
        }
    }

    /**
     * @param elementDisplayName a user-friendly name to the element you are looking for
     * @param anchor             the By to an element that you know for sure is on the page -- web driver will patiently wait for that element
     * @param by                 the By to the element you want to find once the anchor was found on the page
     */
    public boolean isElementEventuallyDisplayed(String elementDisplayName, By anchor, By... by) {
        patientlyFindElement("Anchor for " + elementDisplayName, anchor);
        return isElementDisplayed(elementDisplayName, by);
    }

    public boolean isElementDisplayed(String elementDisplayName, By... bys) {
        try {
            return findElement(elementDisplayName, bys).isDisplayed();
        } catch (DisplayFriendlyNoSuchElementException e) {
            logger.debug(getErrorMessage(elementDisplayName, bys));
            return false;
        } catch (NoSuchElementException e) {
            logger.debug(getErrorMessage(elementDisplayName, bys));
            return false;
        } catch (TimeoutException e) {
            logger.debug(getErrorMessage(elementDisplayName, bys));
            return false;
        }
    }

    public void patientlyWaitUntilNotFound(By by) {
        wait.until(new ElementNotFound(by));
    }

    public String getLastJavascriptAlertMessage() {
        return (String) executeJavascript("return lastAlert;");
    }

    public void swallowJavascriptAlertMessage() {
        executeJavascript("window.alert = function(txt){lastAlert = txt;}");
    }

    public ExtendedWebElement patientlyFindElementWithPageRefresh(String elementDisplayName, By by) {
        wait.until(new ElementIsDisplayedByPageRefresh(by));
        return findElement(elementDisplayName, by);
    }

    public boolean patientlyVerifyTextIsPresentWithPageRefresh(String text) {
        wait.until(new TextIsPresentWithPageRefresh(text));
        return verifyTextIsPresent(text);
    }

    public boolean patientlyVerifyPageTitleContains(String pageTitle) {
        wait.until(new TitleIsPresent(pageTitle));
        return getTitle().contains(pageTitle);
    }

    private class NotFound implements Predicate<By> {
        @Override
        public boolean apply(By by) {
            try {
                ExtendedWebDriver.this.findElement(by);
            } catch (NoSuchElementException e) {
                return true;
            }
            return false;
        }
    }

    public File getScreenshot() {
        if (webDriver instanceof TakesScreenshot) {
            try {
                return ((TakesScreenshot) webDriver).getScreenshotAs(FILE);
            } catch (Exception e) {
                logger.error("Couldn't take a screen shot.", e);
            }
        }
        return null;
    }

    public void switchToPopupWindow() {
        try {
            wait.until(new PopupIsDisplayed());
        } catch (TimeoutException e) {
            throw new RuntimeException("Pop-up window never displayed", e);
        }
        String currentWindowHandle = webDriver.getWindowHandle();
        for (String windowHandle : webDriver.getWindowHandles()) {
            if (!currentWindowHandle.equals(windowHandle)) {
                webDriver.switchTo().window(windowHandle);
                return;
            }
        }
    }

    public void closeAllButTheMainBrowserWindow() {
        List<String> broswerWindowHandles = newArrayList(webDriver.getWindowHandles());
        broswerWindowHandles.remove(baseBrowserWindowHandle);
        for (String broswerWindowHandle : broswerWindowHandles) {
            webDriver.switchTo().window(broswerWindowHandle).close();
        }
        webDriver.switchTo().window(baseBrowserWindowHandle);
    }

    public void rememberBaseBrowserWindowHandle() {
        baseBrowserWindowHandle = webDriver.getWindowHandle();
    }

    public void switchToMainBrowserWindow() {
        webDriver.switchTo().window(baseBrowserWindowHandle);
    }

    public Object executeJavascript(String code, Object... args) {
        JavascriptExecutor js = (JavascriptExecutor) webDriver;
        return js.executeScript(code, args);
    }

    public void simulateBrowserRestart() {
        webDriver.manage().deleteCookieNamed("JSESSIONID");
    }

    private class ElementsAreActive implements ExpectedCondition<Boolean> {
        private By by;
        private String elementDisplayName;

        public ElementsAreActive(String elementDisplayName, By by) {
            this.elementDisplayName = elementDisplayName;
            this.by = by;
        }

        @Override
        public Boolean apply(WebDriver webDriver) {
            List<WebElement> elements = webDriver.findElements(by);
            if (elements.isEmpty()) {
                logger.debug(elementDisplayName + " not found.  We'll keep looking...");
                return false;
            }
            for (WebElement element : elements) {
                if (!new ExtendedWebElement(elementDisplayName, element, ExtendedWebDriver.this).isActive()) {
                    logger.debug("Element was found but is not active: " + elementDisplayName);
                    return false;
                }
            }
            return true;
        }
    }

    private class ElementIsActive implements ExpectedCondition<Boolean> {
        private String elementDisplayName;
        private By by;

        public ElementIsActive(String elementDisplayName, By by) {
            this.elementDisplayName = elementDisplayName;
            this.by = by;
        }

        @Override
        public Boolean apply(WebDriver webDriver) {
            WebElement element = webDriver.findElement(by);
            if (!new ExtendedWebElement(elementDisplayName, element, ExtendedWebDriver.this).isActive()) {
                logger.debug("Element was found but is not active: " + elementDisplayName);
                return false;
            }
            return true;
        }
    }

    public class PopupIsDisplayed implements ExpectedCondition<Boolean> {
        @Override
        public Boolean apply(WebDriver webDriver) {
            return webDriver.getWindowHandles().size() > 1;
        }
    }

    //TODO: Duplicate of same method in ExtendedWebElement.  Extract?
    private void waitASecond() {
        try {
            sleep(1000);
        } catch (InterruptedException e) {
        }
    }

    private class ElementIsDisplayedByPageRefresh implements ExpectedCondition<Boolean> {
        private By by;
        private String text;

        public ElementIsDisplayedByPageRefresh(By by) {
            this.by = by;
        }

        @Override
        public Boolean apply(WebDriver webDriver) {
            try {
                patientlyFindElement(by);
                return true;
            } catch (NoSuchElementException e) {
                navigate().refresh();
                return false;
            }
        }
    }

    private class TextIsPresentWithPageRefresh implements ExpectedCondition<Boolean> {
        private String text;

        public TextIsPresentWithPageRefresh(String text) {
            this.text = text;
        }

        @Override
        public Boolean apply(WebDriver webDriver) {
            if (!patientlyVerifyTextIsPresent(text)) {
                navigate().refresh();
                return false;
            }
            return true;
        }
    }

    private class TitleIsPresent implements ExpectedCondition<Boolean> {
        private String title;

        public TitleIsPresent(String title) {
            this.title = title;
        }

        @Override
        public Boolean apply(WebDriver webDriver) {
            return getTitle().contains(title);
        }
    }
}

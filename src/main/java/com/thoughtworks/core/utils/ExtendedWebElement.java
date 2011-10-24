package com.thoughtworks.core.utils;

import com.thoughtworks.core.web.ExtendedWebDriver;
import com.google.common.base.Predicate;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Select;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static com.thoughtworks.core.utils.CoreUtils.getLoggerFor;
import static com.thoughtworks.core.utils.DisplayFriendlyNoSuchElementException.getErrorMessage;
import static com.google.common.collect.Iterables.all;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.transform;
import static java.lang.System.currentTimeMillis;
import static java.lang.Thread.sleep;
import static org.apache.commons.lang.StringEscapeUtils.unescapeHtml;
import static org.apache.commons.lang.StringUtils.isNotEmpty;
import static org.apache.commons.lang.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang.builder.HashCodeBuilder.reflectionHashCode;
import static org.apache.commons.lang.builder.ToStringBuilder.reflectionToString;
import static org.apache.commons.lang.builder.ToStringStyle.SHORT_PREFIX_STYLE;

public class ExtendedWebElement implements WebElement {
    private WebElement webElement;
    private ExtendedWebDriver extendedWebDriver;
    private String elementDisplayName;

    private final Logger logger = CoreUtils.getLoggerFor(this);
    private static final long DEFAULT_MAX_WAIT = 30000;

    public ExtendedWebElement(String elementDisplayName, WebElement webElement, ExtendedWebDriver extendedWebDriver) {
        this.elementDisplayName = elementDisplayName;
        this.webElement = webElement;
        this.extendedWebDriver = extendedWebDriver;

        loadUserExtensions();
        try {
            extendedWebDriver.executeJavascript("WEBE.highlight_with_webdriver(arguments[0]);", webElement);
        } catch (StaleElementReferenceException e) {
            // it is possible that the element has already been removed from the DOM
        }
    }

    private void loadUserExtensions() {
        try {
            InputStream inputStream  = getClass().getClassLoader().getResourceAsStream("config/user-extensions.js");
            String userExtensions = IOUtils.toString(inputStream, "utf-8");
            extendedWebDriver.executeJavascript(userExtensions);
        } catch (IOException e) {
            throw new RuntimeException("Could not load the javascript user extensions into the browser", e);
        }
    }

    @Override
    public void click() {
        webElement.click();
    }

    @Override
    public void submit() {
        webElement.submit();
    }

    @Override
    public void sendKeys(CharSequence... charSequences) {
        webElement.sendKeys(charSequences);
    }

    @Override
    public void clear() {
        webElement.clear();
    }

    @Override
    public String getTagName() {
        return webElement.getTagName();
    }

    @Override
    public String getAttribute(String s) {
        return unescapeHtml(webElement.getAttribute(s));
    }

    @Override
    public boolean isSelected() {
        return webElement.isSelected();
    }

    @Override
    public boolean isEnabled() {
        return webElement.isEnabled();
    }

    @Override
    public String getText() {
        return unescapeHtml(webElement.getText());
    }

    public boolean isDisplayed() {
        return webElement.isDisplayed();
    }

    @Override
    public Point getLocation() {
        return webElement.getLocation();
    }

    @Override
    public Dimension getSize() {
        return webElement.getSize();
    }

    @Override
    public String getCssValue(String propertyName) {
        return webElement.getCssValue(propertyName);
    }

    @Override
    @Deprecated
    public List<WebElement> findElements(By by) {
        return webElement.findElements(by);
    }

    public Select asSelectable() {
        return new Select(this);
    }

    public List<ExtendedWebElement> findElements(String elementDisplayName, By by) {
        try {
            return transform(webElement.findElements(by), new ToExtendedWebElement(elementDisplayName, extendedWebDriver));
        } catch (NoSuchElementException x) {
            throw new DisplayFriendlyNoSuchElementException(x, elementDisplayName, by);
        }
    }

    @Override
    @Deprecated
    public ExtendedWebElement findElement(By by) {
        return new ExtendedWebElement("Needs a name", webElement.findElement(by), extendedWebDriver);
    }

    public ExtendedWebElement findElement(String elementDisplayName, By by) {
        try {
            return new ExtendedWebElement(elementDisplayName, webElement.findElement(by), extendedWebDriver);
        } catch (NoSuchElementException x) {
            throw new DisplayFriendlyNoSuchElementException(x, elementDisplayName, by);
        }
    }

    public ExtendedWebElement findElement(String elementDisplayName, By... bys) {
        Throwable lastException = null;
        for (By by : bys) {
            try {
                return new ExtendedWebElement(elementDisplayName, webElement.findElement(by), extendedWebDriver);
            } catch (NoSuchElementException x) {
                lastException = x;
            }
        }
        throw new DisplayFriendlyNoSuchElementException(lastException, elementDisplayName, bys);
    }

    public void clearAndSendKeys(String s) {
        clear();
        sendKeys(s);
    }

    public void clickWhile(ExpectedCondition<Boolean> condition) {
        long cutOffTime = getCutOffTime();
        while (condition.apply(extendedWebDriver)) {
            clickElementIfItIsStillOnThePage();
            waitASecond();
            if (cutOffTime < currentTimeMillis()) {
                throw new TimeoutException("Failed to click through " + elementDisplayName);
            }
        }
    }

    public void clickUntil(ExpectedCondition<Boolean>... conditions) {
        long cutOffTime = getCutOffTime();
        while (all(newArrayList(conditions), new NotTrue())) {
            clickElementIfItIsStillOnThePage();
            waitASecond();
            if (cutOffTime < currentTimeMillis()) {
                throw new TimeoutException("Failed to click through " + elementDisplayName);
            }
        }
    }

    public void clickWhen(ExpectedCondition<Boolean> condition) {
        long cutOffTime = getCutOffTime();
        while (!condition.apply(extendedWebDriver)) {
            waitASecond();
            if (cutOffTime < currentTimeMillis()) {
                throw new TimeoutException("Failed to click through " + elementDisplayName);
            }
        }
        clickElementIfItIsStillOnThePage();
    }

    private long getCutOffTime() {
        return currentTimeMillis() + DEFAULT_MAX_WAIT;
    }

    private void clickElementIfItIsStillOnThePage() {
        try {
            click();
        } catch (StaleElementReferenceException ignore) {
        }
    }

    private void waitASecond() {
        try {
            sleep(2000);
        } catch (InterruptedException e) {
        }
    }

    public Boolean isActive() {
        try {
            return isEnabled() && isDisplayed();
        } catch (Exception e) {
            System.err.println("Swallowing error: " + e.getMessage());
            return false;
        }
    }

    public boolean verifyTextIsPresent(String text) {
        return getText().contains(text);
    }

    public boolean isDisabled() {
        return !isEnabled();
    }

    public boolean isElementDisplayed(String elementDisplayName, By by) {
        try {
            ExtendedWebElement element = findElement(by);
            return element.isDisplayed();
        } catch (NoSuchElementException e) {
            logger.debug(DisplayFriendlyNoSuchElementException.getErrorMessage(elementDisplayName, by));
        }

        return false;
    }

    public void setCheckBoxSelected(boolean selected) {
        if(!(selected && this.isSelected())){
            this.click();
        }
    }

    private class NotTrue implements Predicate<ExpectedCondition> {
        @Override
        public boolean apply(ExpectedCondition condition) {
            return !(Boolean) condition.apply(extendedWebDriver);
        }
    }

    @Override
    public boolean equals(Object o) {
        return reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return reflectionToString(this, SHORT_PREFIX_STYLE);
    }
}

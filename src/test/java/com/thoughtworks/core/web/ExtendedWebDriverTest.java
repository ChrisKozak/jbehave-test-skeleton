package com.thoughtworks.core.web;

import com.thoughtworks.core.utils.DisplayFriendlyNoSuchElementException;
import com.thoughtworks.core.utils.ExtendedWebElement;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class ExtendedWebDriverTest {
    private static final long ONE_SECOND = 1;
    private static final By EXISTING_ELEMENT_ID = By.id("EXISTING_ELEMENT");
    private static final By NON_EXISTING_ELEMENT_ID = By.id("NON_EXISTING_ELEMENT");
    private static final String EXISTING_ELEMENT_DISPLAY_NAME = "Existing Element";
    private static WebElement EXISTING_ELEMENT;

    private WebDriver webDriver;
    private ExtendedWebDriver extendedWebDriver;

    @Before
    public void setup() {
        webDriver = mock(WebDriver.class);
        WebDriverWait webDriverWait = new WebDriverWait(webDriver, ONE_SECOND);
        extendedWebDriver = disallowWebClientToExecuteJavascript(new ExtendedWebDriver(webDriver, webDriverWait));
    }

    @Test
    public void shouldReturnAnExistingElement() {
        EXISTING_ELEMENT = mock(WebElement.class);
        when(webDriver.findElement(EXISTING_ELEMENT_ID)).thenReturn(EXISTING_ELEMENT);

        assertThat(extendedWebDriver.findElement(EXISTING_ELEMENT_DISPLAY_NAME, EXISTING_ELEMENT_ID), is(new ExtendedWebElement(EXISTING_ELEMENT_DISPLAY_NAME, EXISTING_ELEMENT, extendedWebDriver)));
    }

    @Test(expected = DisplayFriendlyNoSuchElementException.class)
    public void shouldThrowNoSuchElementExceptionForNonExistingElement() {
        when(webDriver.findElement(NON_EXISTING_ELEMENT_ID)).thenThrow(new NoSuchElementException(""));

        extendedWebDriver.findElement(EXISTING_ELEMENT_DISPLAY_NAME, NON_EXISTING_ELEMENT_ID);
    }

    private ExtendedWebDriver disallowWebClientToExecuteJavascript(ExtendedWebDriver extendedWebDriver) {
        ExtendedWebDriver spy = spy(extendedWebDriver);
        doReturn(nullValue()).when(spy).executeJavascript(any(String.class), any(Object.class));
        doReturn(nullValue()).when(spy).executeJavascript(any(String.class));
        return spy;
    }

}

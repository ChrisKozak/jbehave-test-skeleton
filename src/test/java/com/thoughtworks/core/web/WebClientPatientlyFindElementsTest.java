package com.thoughtworks.core.web;

import com.thoughtworks.core.utils.ExtendedWebElement;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import static com.thoughtworks.core.web.ExtendedWebDriver.NEEDS_A_NAME;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class WebClientPatientlyFindElementsTest {
    private WebDriver webDriver;
    private ExtendedWebDriver extendedWebDriver;
    private static final long ONE_SECOND = 2;

    @Before
    public void setup() {
        webDriver = mock(WebDriver.class);
        WebDriverWait webDriverWait = new WebDriverWait(webDriver, ONE_SECOND);
        extendedWebDriver = disallowWebClientToExecuteJavascript(new ExtendedWebDriver(webDriver, webDriverWait));
    }

    private ExtendedWebDriver disallowWebClientToExecuteJavascript(ExtendedWebDriver extendedWebDriver) {
        ExtendedWebDriver spy = spy(extendedWebDriver);
        doReturn(nullValue()).when(spy).executeJavascript(any(String.class), any(Object.class));
        doReturn(nullValue()).when(spy).executeJavascript(any(String.class));
        return spy;
    }

    @Test
    public void patientlyFindElementsShouldReturnAllWebElementsWhenAllAreActive() {
        WebElement activeWebElement1 = activeWebElement();
        WebElement activeWebElement2 = activeWebElement();
        By allActiveBy = By.className("allActive");
        when(webDriver.findElements(allActiveBy)).thenReturn(asList((WebElement) activeWebElement1, activeWebElement2));

        ExtendedWebElement expectedNamelessExtendedWebElement1 = new ExtendedWebElement(NEEDS_A_NAME, activeWebElement1, extendedWebDriver);
        ExtendedWebElement expectedNamelessExtendedWebElement2 = new ExtendedWebElement(NEEDS_A_NAME, activeWebElement2, extendedWebDriver);

        assertThat(extendedWebDriver.patientlyFindElements(allActiveBy), hasItems(expectedNamelessExtendedWebElement1, expectedNamelessExtendedWebElement2));
    }

    @Test
    public void patientlyFindAnyElementsShouldReturnAllWebElementsWhenAllAreActive() {
        WebElement activeWebElement1 = activeWebElement();
        WebElement activeWebElement2 = activeWebElement();
        By allActiveBy = By.className("allActive");
        when(webDriver.findElements(allActiveBy)).thenReturn(asList((WebElement) activeWebElement1, activeWebElement2));

        String elementDisplayName = "ALL ACTIVE ELEMENTS NAME";
        ExtendedWebElement expectedNamedExtendedWebElement1 = new ExtendedWebElement(elementDisplayName, activeWebElement1, extendedWebDriver);
        ExtendedWebElement expectedNamedExtendedWebElement2 = new ExtendedWebElement(elementDisplayName, activeWebElement2, extendedWebDriver);

        assertThat(extendedWebDriver.patientlyFindElements(elementDisplayName, allActiveBy), hasItems(expectedNamedExtendedWebElement1, expectedNamedExtendedWebElement2));
    }

    @Test
    public void patientlyFindElementsShouldReturnOnlyActiveWebElementsWhenSomeAreInactive() {
        WebElement activeWebElement1 = activeWebElement();
        WebElement activeWebElement2 = inactiveWebElement();
        By allActiveBy = By.className("allActive");
        when(webDriver.findElements(allActiveBy)).thenReturn(asList((WebElement) activeWebElement1, activeWebElement2));

        ExtendedWebElement activeNamelessExtendedWebElement = new ExtendedWebElement(NEEDS_A_NAME, activeWebElement1, extendedWebDriver);
        ExtendedWebElement inactiveNamelessExtendedWebElement = new ExtendedWebElement(NEEDS_A_NAME, activeWebElement2, extendedWebDriver);

        assertThat(extendedWebDriver.patientlyFindElements(allActiveBy), hasItems(activeNamelessExtendedWebElement));
        assertThat(extendedWebDriver.patientlyFindElements(allActiveBy), not(hasItems(inactiveNamelessExtendedWebElement)));
    }

    @Test
    public void patientlyFindAnyElementsShouldReturnOnlyActiveWebElementsWhenSomeAreInactive() {
        WebElement activeWebElement1 = activeWebElement();
        WebElement activeWebElement2 = inactiveWebElement();
        By allActiveBy = By.className("allActive");
        when(webDriver.findElements(allActiveBy)).thenReturn(asList((WebElement) activeWebElement1, activeWebElement2));

        String elementDisplayName = "ALL ACTIVE ELEMENTS NAME";
        ExtendedWebElement activeNamedExtendedWebElement = new ExtendedWebElement(elementDisplayName, activeWebElement1, extendedWebDriver);
        ExtendedWebElement inactiveNamedExtendedWebElement = new ExtendedWebElement(elementDisplayName, activeWebElement2, extendedWebDriver);

        assertThat(extendedWebDriver.patientlyFindElements(elementDisplayName, allActiveBy), hasItems(activeNamedExtendedWebElement));
        assertThat(extendedWebDriver.patientlyFindElements(elementDisplayName, allActiveBy), not(hasItems(inactiveNamedExtendedWebElement)));
    }

    @Test
    public void patientlyFindElementsShouldReturnAnEmptyListWhenAllAreInactive() {
        WebElement inactiveWebElement1 = inactiveWebElement();
        WebElement inactiveWebElement2 = inactiveWebElement();
        By allInactiveBy = By.className("allInactive");
        when(webDriver.findElements(allInactiveBy)).thenReturn(asList((WebElement) inactiveWebElement1, inactiveWebElement2));

        assertThat(extendedWebDriver.patientlyFindElements(allInactiveBy).size(), is(0));
    }

    @Test
    public void patientlyFindAnyElementsShouldReturnAnEmptyListWhenAllAreInactive() {
        WebElement inactiveWebElement1 = inactiveWebElement();
        WebElement inactiveWebElement2 = inactiveWebElement();
        By allInactiveBy = By.className("allInactive");
        when(webDriver.findElements(allInactiveBy)).thenReturn(asList((WebElement) inactiveWebElement1, inactiveWebElement2));

        assertThat(extendedWebDriver.patientlyFindElements("ELEMENTS DISPLAY NAME", allInactiveBy).size(), is(0));
    }

    @Test
    public void patientlyFindElementsShouldWaitForAllElementsToBecomeActiveBeforeReturning() {
        WebElement temporarilyInactiveWebElement = temporarilyInactiveWebElement();
        WebElement inactiveWebElement = inactiveWebElement();
        WebElement activeWebElement = activeWebElement();
        By elementsStillRendering = By.className("all");
        when(webDriver.findElements(elementsStillRendering)).thenReturn(asList((WebElement) temporarilyInactiveWebElement, inactiveWebElement, activeWebElement));

        ExtendedWebElement expectedActiveExtendedWebElement = new ExtendedWebElement(NEEDS_A_NAME, activeWebElement, extendedWebDriver);
        ExtendedWebElement expectedTemporarilyInactiveExtendedWebElement = new ExtendedWebElement(NEEDS_A_NAME, temporarilyInactiveWebElement, extendedWebDriver);

        assertThat(extendedWebDriver.patientlyFindElements(elementsStillRendering).size(), is(2));
        assertThat(extendedWebDriver.patientlyFindElements(elementsStillRendering), hasItems(expectedActiveExtendedWebElement, expectedTemporarilyInactiveExtendedWebElement));
    }

    @Test
    public void patientlyFindAnyElementsShouldWaitForAllElementsToBecomeActiveBeforeReturning() {
        WebElement temporarilyInactiveWebElement = temporarilyInactiveWebElement();
        WebElement inactiveWebElement = inactiveWebElement();
        WebElement activeWebElement = activeWebElement();
        By elementsStillRendering = By.className("all");
        when(webDriver.findElements(elementsStillRendering)).thenReturn(asList((WebElement) temporarilyInactiveWebElement, inactiveWebElement, activeWebElement));

        String elementDisplayName = "ELEMENTS DISPLAY NAME";
        ExtendedWebElement expectedActiveExtendedWebElement = new ExtendedWebElement(elementDisplayName, activeWebElement, extendedWebDriver);
        ExtendedWebElement expectedTemporarilyInactiveExtendedWebElement = new ExtendedWebElement(elementDisplayName, temporarilyInactiveWebElement, extendedWebDriver);

        assertThat(extendedWebDriver.patientlyFindElements(elementDisplayName, elementsStillRendering).size(), is(2));
        assertThat(extendedWebDriver.patientlyFindElements(elementDisplayName, elementsStillRendering), hasItems(expectedActiveExtendedWebElement, expectedTemporarilyInactiveExtendedWebElement));
    }


    private WebElement inactiveWebElement() {
        WebElement activeWebElement2 = mock(WebElement.class);
        when(activeWebElement2.isEnabled()).thenReturn(false);
        when(activeWebElement2.isDisplayed()).thenReturn(true);
        return activeWebElement2;
    }

    private WebElement activeWebElement() {
        WebElement activeWebElement1 = mock(WebElement.class);
        when(activeWebElement1.isEnabled()).thenReturn(true);
        when(activeWebElement1.isDisplayed()).thenReturn(true);
        return activeWebElement1;
    }

    private WebElement temporarilyInactiveWebElement() {
        WebElement activeWebElement1 = mock(WebElement.class);
        when(activeWebElement1.isEnabled()).thenReturn(false).thenReturn(false).thenReturn(false).thenReturn(false).thenReturn(true);
        when(activeWebElement1.isDisplayed()).thenReturn(true);
        return activeWebElement1;
    }


}

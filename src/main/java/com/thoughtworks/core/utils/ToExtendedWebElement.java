package com.thoughtworks.core.utils;

import com.thoughtworks.core.web.ExtendedWebDriver;
import com.google.common.base.Function;
import org.openqa.selenium.WebElement;

public class ToExtendedWebElement implements Function<WebElement, ExtendedWebElement> {
    private String elementDisplayName;
    private ExtendedWebDriver extendedWebDriver;

    public ToExtendedWebElement(String elementDisplayName, ExtendedWebDriver extendedWebDriver) {
        this.elementDisplayName = elementDisplayName;
        this.extendedWebDriver = extendedWebDriver;
    }

    @Override
    public ExtendedWebElement apply(WebElement webElement) {
        return new ExtendedWebElement(elementDisplayName, webElement, extendedWebDriver);
    }

}


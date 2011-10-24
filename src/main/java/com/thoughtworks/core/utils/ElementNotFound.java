package com.thoughtworks.core.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;

public class ElementNotFound implements ExpectedCondition<Boolean> {
    private By by;

    public ElementNotFound(By by) {
        this.by = by;
    }

    @Override
    public Boolean apply(WebDriver webClient) {
        try {
            webClient.findElement(by);
            return false;
        } catch (NoSuchElementException ignore) {
            return true;
        }
    }
}

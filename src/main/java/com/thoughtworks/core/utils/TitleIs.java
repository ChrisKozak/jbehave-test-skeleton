package com.thoughtworks.core.utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;

public class TitleIs implements ExpectedCondition<Boolean>{
    private String title;

    public TitleIs(String title) {
        this.title = title;
    }

    @Override
    public Boolean apply(WebDriver webDriver) {
        return webDriver.getTitle().equals(title);
    }
}

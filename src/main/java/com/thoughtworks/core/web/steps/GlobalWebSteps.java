package com.thoughtworks.core.web.steps;

import com.thoughtworks.core.utils.UnexpectedAlertException;
import com.thoughtworks.core.web.Browser;
import com.thoughtworks.core.web.ExtendedWebDriver;
import org.jbehave.core.annotations.AfterScenario;
import org.jbehave.core.annotations.AfterStories;
import org.jbehave.core.annotations.BeforeScenario;
import org.jbehave.core.annotations.BeforeStories;
import org.openqa.selenium.Alert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GlobalWebSteps {
    @Autowired
    private Browser browser;

    @Autowired
    private ExtendedWebDriver extendedWebDriver;

    @BeforeStories
    public void rememberBaseWindow() {
        browser.rememberBaseWindowHandle();
    }

    @BeforeScenario
    public void clearBrowserSession() {
        browser.clearSession();
    }

    @AfterStories
    public void cleanUpTestRun() {
        browser.close();
    }

    @AfterScenario
    public void validateThatThereIsNotAnAlert() {
        Alert alert = extendedWebDriver.getAlertOrNull();
        if (alert != null) {
            String alertText = alert.getText();
            alert.dismiss(); // so it doesn't cause future harm
            throw new UnexpectedAlertException(alertText);
        }
    }

}

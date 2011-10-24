package com.thoughtworks.functionaltests.pages;

import com.thoughtworks.core.utils.ExtendedWebElement;
import com.thoughtworks.core.web.AbstractComponent;
import org.openqa.selenium.By;
import org.springframework.stereotype.Component;

@Component
public class GoogleHomepage extends AbstractComponent{
    public void searchFor(String searchTerm) {
        searchTextBox().clearAndSendKeys(searchTerm);
        googleSearchButton().click();
    }



    //Locators below this line


    private ExtendedWebElement searchTextBox() {
        return extendedWebDriver.patientlyFindElement("Search text box", By.id("lst-ib"));
    }

    private ExtendedWebElement googleSearchButton() {
        return extendedWebDriver.patientlyFindElement("'Google Search' button", By.name("btnG"));
    }
}

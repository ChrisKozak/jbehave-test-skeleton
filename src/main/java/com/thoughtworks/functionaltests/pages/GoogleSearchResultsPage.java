package com.thoughtworks.functionaltests.pages;

import com.thoughtworks.core.utils.ExtendedWebElement;
import com.thoughtworks.core.web.AbstractComponent;
import org.openqa.selenium.By;
import org.springframework.stereotype.Component;

import java.util.List;

import static ch.lambdaj.Lambda.extractProperty;

@Component
public class GoogleSearchResultsPage extends AbstractComponent {
    public List<String> allSearchResults() {
        return extractProperty(searchResults(), "text");
    }

    private List<ExtendedWebElement> searchResults() {
        return extendedWebDriver.findElementsOnceAnchorIsDisplayed("Search results links", By.id("resultStats"), By.className("l"));
    }
}

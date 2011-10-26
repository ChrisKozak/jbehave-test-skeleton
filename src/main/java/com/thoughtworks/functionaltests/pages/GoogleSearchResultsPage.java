package com.thoughtworks.functionaltests.pages;

import com.google.common.base.Function;
import com.thoughtworks.core.utils.ExtendedWebElement;
import com.thoughtworks.core.web.AbstractComponent;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.springframework.stereotype.Component;

import java.util.List;

import static ch.lambdaj.Lambda.extractProperty;

@Component
public class GoogleSearchResultsPage extends AbstractComponent {
    public List<String> allSearchResults() {
        return extractProperty(searchResults(), "text");
    }

    public void getNextResults() {
        nextLink().click();
        wait.until(new CurrentPageIs(2));
    }



    private List<ExtendedWebElement> searchResults() {
        return extendedWebDriver.findElementsOnceAnchorIsDisplayed("Search results links", By.id("resultStats"), By.className("l"));
    }

    private ExtendedWebElement nextLink() {
        return extendedWebDriver.patientlyFindElement("Next Link", By.linkText("Next"));
    }

    private class CurrentPageIs implements ExpectedCondition<Boolean> {
        private Integer pageNumber;

        public CurrentPageIs(Integer pageNumber) {
            this.pageNumber = pageNumber;
        }

        @Override
        public Boolean apply(WebDriver webDriver) {
            WebElement currentPageNumber;
            try{
                currentPageNumber = webDriver.findElement(By.className("cur"));
            }catch(NoSuchElementException x){
                return false;
            }
            return currentPageNumber.getText().equals(pageNumber.toString());
        }
    }
}

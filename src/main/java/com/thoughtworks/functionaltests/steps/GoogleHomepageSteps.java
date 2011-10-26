package com.thoughtworks.functionaltests.steps;

import com.thoughtworks.core.utils.GlobalClipboard;
import com.thoughtworks.core.web.Browser;
import com.thoughtworks.functionaltests.pages.GoogleHomepage;
import com.thoughtworks.functionaltests.pages.GoogleSearchResultsPage;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;

@Component
public class GoogleHomepageSteps {
    @Autowired
    private GoogleHomepage googleHomepage;
    @Autowired
    private Browser browser;
    @Autowired
    private GoogleSearchResultsPage googleSearchResultsPage;
    @Autowired
    private GlobalClipboard globalClipboard;


    @Given("I am visiting the Googles")
    public void visitTheGoogles(){
        browser.navigateToHomepage();
    }

    @When("I search for '$searchTerm'")
    public void searchFor(String searchTerm){
        googleHomepage.searchFor(searchTerm);
        globalClipboard.rememberLastSearchTerm(searchTerm);
    }

    @Then("I should see my profile")
    public void verifyChrisKozakProfileIsListed(){
        assertThat(googleSearchResultsPage.allSearchResults(), hasItem("Chris Kozak - Google Profile"));
    }
}

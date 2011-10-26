package com.thoughtworks.functionaltests.steps;

import com.thoughtworks.core.utils.AssertionLog;
import com.thoughtworks.core.utils.GlobalClipboard;
import com.thoughtworks.functionaltests.pages.GoogleSearchResultsPage;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

@Component
public class GoogleSearchResultsPageSteps {
    @Autowired
    private AssertionLog assertionLog;
    @Autowired
    private GoogleSearchResultsPage googleSearchResultsPage;
    @Autowired
    private GlobalClipboard globalClipboard;

    @Then("I should see my profile")
    public void verifyChrisKozakProfileIsListed(){
        assertionLog.ensureThat(googleSearchResultsPage.allSearchResults(), hasItem("Chris Kozak - Google Profile"));
    }

    @When("I choose to see more search results")
    public void seeNextResutlts(){
        globalClipboard.rememberLastSearchResults(googleSearchResultsPage.allSearchResults());
        googleSearchResultsPage.getNextResults();
    }

    @Then("I should see different search results")
    public void verifyDifferentSearchResults(){
        assertThat(googleSearchResultsPage.allSearchResults(), not(equalTo(globalClipboard.getLastSearchResults())));
    }
}

package com.thoughtworks.core.web.steps;

import com.thoughtworks.core.web.Browser;
import org.jbehave.core.annotations.Alias;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BrowserSteps {
    @Autowired
    private Browser browser;

    @When("I restart the browser")
    @Given("I restart the browser")
    public void restartBrowser() {
        browser.restart();
        accessTheSite();
    }

    @When("I navigate back")
    public void navigateBack() {
        browser.back();
    }

    @When("I navigate back $number times")
    public void navigateBack(int number) {
        for (int i = 0; i < number; i++) {
            navigateBack();
        }
    }

    @When("I reload the page")
    public void reloadCurrentPage() {
        browser.refresh();
    }

    @Then("the page source should include \"$message\"")
    public boolean verifyMessageInPageSource(String message) {
        return browser.getPageSource().contains(message);
    }

    @Then("the template name for the page should be '$name'")
    public boolean verifyTemplateName(String name) {
        return browser.getPageSource().contains("var templateName = '" + name + "'");
    }

    @Given("I am on the Home page")
    @When("I access the site")
    @Alias("I navigate to the home page")
    public void accessTheSite() {
        browser.navigateToHomepage();
    }
}

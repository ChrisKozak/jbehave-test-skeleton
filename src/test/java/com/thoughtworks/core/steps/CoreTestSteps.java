package com.thoughtworks.core.steps;

import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@Component
public class CoreTestSteps {
    private List<String> stringList;

    @Given("I have a list")
    public void haveAList() {
        stringList = new ArrayList<String>();
    }

    @When("I add two items to the list")
    public void addTwoItemsToList() {
        stringList.add("Item1");
        stringList.add("Item2");
    }

    @Then("the list should contain two items")
    public void listShouldContainTwoItems() {
        assertThat(stringList.size(), is(2));
    }
}

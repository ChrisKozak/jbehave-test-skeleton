package com.thoughtworks.core.utils;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;

// Understands the comparison between expected behavior and what actually happened
public class SoftAssertion<T> {
    private T actual;
    private Matcher<T> matcher;
    private Throwable failure;
    private String representation;
    private Boolean reported = false;

    public SoftAssertion(String message, T actual, Matcher<T> matcher) {
        this.actual = actual;
        this.matcher = matcher;
        this.representation = "ASSERTION PASSED - " + getRepresentation(message, actual, matcher);
    }

    public SoftAssertion(String message, T actual, Matcher<T> matcher, Throwable failure) {
        this.actual = actual;
        this.matcher = matcher;
        this.failure = failure;
        this.representation = "ASSERTION FAILED - " + getRepresentation(message, actual, matcher);
    }

    private String getRepresentation(String message, T actual, Matcher<T> matcher) {
        Description description= new StringDescription();
        description.appendText("\n[" + message + "]");
        description.appendText("\nExpected: ");
        description.appendDescriptionOf(matcher);
        description.appendText("\n     got: ");
        description.appendValue(actual);
        description.appendText("\n");
        return description.toString();
    }

    @Override
    public String toString() {
        return representation;
    }

    public boolean passed() {
        return failure == null;
    }

    public Throwable getFailure() {
        return failure;
    }

    public void markAsReported() {
        reported = true;
    }

    public boolean isReported(){
        return reported;
    }

    public Boolean isNewFailure() {
        return !(passed() || isReported());
    }

    public boolean failed() {
        return !passed();
    }

}

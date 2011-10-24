package com.thoughtworks.core.steps;

public class ScenarioFailedFromAssertionFailuresException extends RuntimeException {
    public ScenarioFailedFromAssertionFailuresException(String message) {
        super(message);
    }
}

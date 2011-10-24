package com.thoughtworks.core.utils;

/**
 * Unhandled alerts can cause future tests to hang.
 */
public class UnexpectedAlertException extends RuntimeException {
    public UnexpectedAlertException(String alertText) {
        super("We found an unexpected alert. Message: '"+ alertText +"'.  This message should be handled as part of the test.");
    }
}

package com.thoughtworks.core.utils;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class StackTraceAssertionListener extends AssertionListener {
    @Override
    public String afterStepFailed(AssertionLog assertionLog, String storyName, String step, Throwable failure) {
        resetUUID();
        return "<a href=\"#\" onclick='var trace = document.getElementById(\"stackTrace" + currentUUID() + "\"); trace.style.display = trace.style.display == \"none\" ? \"block\" : \"none\"; return false;'>Toggle stack trace</a><pre id=\"stackTrace" + currentUUID() + "\" style=\"display:none;\" class=\"failure\">\n" + stackTrace(failure.getCause()) + "\n</pre>";
    }

    private String stackTrace(Throwable cause) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        cause.printStackTrace(new PrintStream(out));
        return out.toString();
    }

    @Override
    public Integer getIndex() {
        return 1;
    }
}

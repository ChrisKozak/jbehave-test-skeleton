package com.thoughtworks.core.utils;

import org.apache.log4j.Logger;
import org.hamcrest.Matcher;
import org.springframework.stereotype.Component;

import java.util.*;

import static ch.lambdaj.Lambda.*;
import static com.thoughtworks.core.utils.matchers.BooleanLambda.all;
import static com.thoughtworks.core.utils.matchers.BooleanLambda.any;
import static com.google.common.collect.Lists.reverse;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

// Understands the soft assertions that have been made throughout the scenario
@Component
public class AssertionLog {

    private Logger logger = CoreUtils.getLoggerFor(this);
    private List<SoftAssertion> assertions = new ArrayList<SoftAssertion>();
    private Set<AssertionListener> listeners = new TreeSet<AssertionListener>();

    public boolean hasAnyFailures() {
        return !all(assertions, having(on(SoftAssertion.class).passed()));
    }

    public boolean hasAnyNewFailures() {
        return any(assertions, having(on(SoftAssertion.class).isNewFailure()));
    }

    public void addAssertionListeners(AssertionListener... listeners) {
        this.listeners.addAll(asList(listeners));
    }

    public void add(SoftAssertion assertion) {
        assertions.add(assertion);

        if (assertion.failed()) {
            logger.error(assertion);
            for (AssertionListener listener : listeners) {
                listener.afterAssertFailed();
            }
        }
    }

    public void markAssertionsAsSeen() {
        for (SoftAssertion assertion : assertions) {
            assertion.markAsReported();
        }
    }

    public SoftAssertion firstNewFailedAssertion() {
        return CoreUtils.selectFirst(assertions, having(on(SoftAssertion.class).isNewFailure()));
    }

    public void clear() {
        assertions.clear();
    }

    public <T> void ensureQuietlyThat(String message, T actual, org.hamcrest.Matcher<T> matcher) {
        ensure(message, actual, matcher, false);
    }

    public <T> void ensureThat(String message, T actual, org.hamcrest.Matcher<T> matcher) {
        ensure(message, actual, matcher, true);
    }

    public <T> void ensureThat(T actual, org.hamcrest.Matcher<T> matcher) {
        ensureThat(null, actual, matcher);
    }

    private <T> void ensure(String message, T actual, Matcher<T> matcher, boolean logSuccess) {
        try {
            assertThat(message, actual, matcher);
            if (logSuccess) {
                add(new SoftAssertion(message, actual, matcher));
            }
        } catch (AssertionError e) {
            add(new SoftAssertion(message, actual, matcher, e));
        }
    }

    public int size() {
        return assertions.size();
    }

    public Collection<AssertionListener> getListeners() {
        return listeners;
    }

    public List<SoftAssertion> getNewAssertions() {
        return filter(not(having(on(SoftAssertion.class).isReported())), assertions);
    }

    public SoftAssertion firstAssertionFailure() {
        return CoreUtils.selectFirst(assertions, not(having(on(SoftAssertion.class).passed())));
    }

    public Integer failureCount() {
        return select(assertions, having(on(SoftAssertion.class).failed())).size();
    }

    public SoftAssertion getLastAssertionFailure() {
        return CoreUtils.selectFirst(reverse(assertions), having(on(SoftAssertion.class).failed()));
    }
}

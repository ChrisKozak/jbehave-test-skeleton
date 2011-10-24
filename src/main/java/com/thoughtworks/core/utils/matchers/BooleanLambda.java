package com.thoughtworks.core.utils.matchers;

import org.hamcrest.Matcher;

import java.util.Iterator;

public class BooleanLambda {
    public static <T> Boolean any(Iterable<T> iterable, Matcher<?> matcher) {
        Iterator<? extends T> iterator = iterable.iterator();
        while (iterator.hasNext()) {
            T item = iterator.next();
            if (matcher.matches(item)) {
                return true;
            }
        }
        return false;
    }

    public static <T> Boolean all(Iterable<T> iterable, Matcher<?> matcher) {
        Iterator<? extends T> iterator = iterable.iterator();
        while (iterator.hasNext()) {
            T item = iterator.next();
            if (!matcher.matches(item)) {
                return false;
            }
        }
        return true;
    }

    public static <T> Boolean none(Iterable<T> iterable, Matcher<?> matcher) {
        return !any(iterable, matcher);
    }
}


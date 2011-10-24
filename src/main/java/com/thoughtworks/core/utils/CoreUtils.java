package com.thoughtworks.core.utils;

import ch.lambdaj.Lambda;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.hamcrest.Matcher;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Collection;

import static org.apache.log4j.Logger.getLogger;

public class CoreUtils {

    //Hiding Constructor
    private CoreUtils() {}

    public static final Logger getLoggerFor(Object object) {
        return getLoggerFor(object.getClass());
    }

    public static Logger getLoggerFor(Class klass) {
        return getLogger(klass.getSimpleName());
    }

    public static File resolveBuildDirectory() {
        return new File(System.getProperty("jbehave.report.dir") == null ? "." : System.getProperty("jbehave.report.dir"));
    }

    public static File resolveDirectory(String directoryName) {
        File directory = new File(resolveBuildDirectory(), directoryName);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        return directory;
    }

    public static void nullifyAllDeclaredFields(Object object) {
        try {
            for (Field declaredField : object.getClass().getDeclaredFields()) {
                declaredField.setAccessible(true);
                declaredField.set(object, null);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T selectFirst(Collection<T> iterable, Matcher<?> matcher) {
        return Lambda.<T>selectFirst(iterable, matcher);
    }


    public static String unescapeHtml(String text) {
        if(text == null) {
            return null;
        }
        // Not sure why but when using StringEscapeUtils.unescapeHtml certain escape code #153; are unescaped as wrongly
        // So here we pre-process the string before unescaping the rest:
        text = text.replaceAll("&#151;", "\u2014"); //is unescaped as '\u0097' where I would expect '\u2014'
        text = text.replaceAll("&#153;", "\u2122"); //is unescaped as '\u0099' where I would expect '\u2122'
        return StringEscapeUtils.unescapeHtml(text);
    }
}

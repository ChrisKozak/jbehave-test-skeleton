package com.thoughtworks.core.utils;

import org.openqa.selenium.By;

import java.util.List;

import static ch.lambdaj.Lambda.join;

public class DisplayFriendlyNoSuchElementException extends RuntimeException {

    private String elementDisplayName;
    private By[] elementIdentifiers;
    private String customFindDescription;

    public DisplayFriendlyNoSuchElementException(Throwable cause, String elementDisplayName, By... elementIdentifiers) {
        super(cause);
        this.elementDisplayName = elementDisplayName;
        this.elementIdentifiers = elementIdentifiers;
    }

    public DisplayFriendlyNoSuchElementException(Throwable cause, String elementDisplayName, List<By> allBys) {
        this(cause, elementDisplayName, allBys.toArray(new By[0]));
    }

    public DisplayFriendlyNoSuchElementException(String elementDisplayName, By... elementIdentifiers) {
        super(join(elementIdentifiers, ", "));
        this.elementDisplayName = elementDisplayName;
        this.elementIdentifiers = elementIdentifiers;
    }

    public DisplayFriendlyNoSuchElementException(String elementDisplayName, String customFindDescription) {
        this.elementDisplayName = elementDisplayName;
        this.customFindDescription = customFindDescription;
    }

    @Override
    public String getMessage() {
        if(null == elementIdentifiers){
            return getErrorMessage(elementDisplayName, customFindDescription);
        }
        return getErrorMessage(elementDisplayName, elementIdentifiers);
    }

    public static String getErrorMessage(String elementDisplayName, By... elementIdentifiers) {
        return "Could not find '" + elementDisplayName + "' on the page with identifier(s) " + join(elementIdentifiers, ", ");
    }

    public static String getErrorMessage(String elementDisplayName, String customFindDescription) {
        return "Could not find '" + elementDisplayName + "' on the page with custom find: " + customFindDescription;
    }
}

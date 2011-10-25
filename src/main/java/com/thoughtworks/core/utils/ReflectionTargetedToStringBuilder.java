package com.thoughtworks.core.utils;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.builder.ToStringStyle.SHORT_PREFIX_STYLE;

public class ReflectionTargetedToStringBuilder extends ReflectionToStringBuilder {

    public ReflectionTargetedToStringBuilder(Object object, ToStringStyle style) {
        super(object, style);
    }

    public static String reflectionTargetedToString(Object object) {
        return reflectionTargetedToString(object, SHORT_PREFIX_STYLE);
    }

    public static String reflectionTargetedToString(Object object, ToStringStyle style) {
        return new ReflectionTargetedToStringBuilder(object, style).toString();
    }

    @Override
    protected boolean accept(Field field) {
        return (super.accept(field) && getValueForField(field) != null && !isEmptyCollection(field) && !isEmptyMap(field) && !isBlankString(field));
    }

    private boolean isBlankString(Field field) {
        if (field.getType().equals(String.class)) {
            String string = (String) getValueForField(field);
            return isBlank(string);
        }

        return false;
    }

    private boolean isEmptyMap(Field field) {
        if (Map.class.isAssignableFrom(field.getType())) {
            Map map = (Map) getValueForField(field);
            return map.isEmpty();
        }

        return false;
    }

    private boolean isEmptyCollection(Field field) {
        if (Collection.class.isAssignableFrom(field.getType())) {
            Collection collection = (Collection) getValueForField(field);
            return collection.isEmpty();
        }

        return false;
    }

    private Object getValueForField(Field field) {
        Object value = null;
        try {
            value = super.getValue(field);
        } catch (IllegalAccessException e) { }
        return value;
    }
}

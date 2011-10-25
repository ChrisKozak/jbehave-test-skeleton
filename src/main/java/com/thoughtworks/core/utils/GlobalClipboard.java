package com.thoughtworks.core.utils;

import org.springframework.stereotype.Component;

import static com.thoughtworks.core.utils.CustomToStringStyle.SHORT_PREFIX_MULTI_LINE_STYLE;
import static com.thoughtworks.core.utils.ReflectionTargetedToStringBuilder.reflectionTargetedToString;

@Component
public class GlobalClipboard {
    @Override
    public String toString() {
        return reflectionTargetedToString(this, SHORT_PREFIX_MULTI_LINE_STYLE);
    }

}

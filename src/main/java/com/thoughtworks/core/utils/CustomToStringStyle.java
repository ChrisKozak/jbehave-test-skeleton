package com.thoughtworks.core.utils;

import org.apache.commons.lang.builder.ToStringStyle;

public class CustomToStringStyle {

    public static final ToStringStyle MINIMAL_STYLE = new MinimalToStringStyle();
    public static final ToStringStyle SHORT_PREFIX_MULTI_LINE_STYLE = new ShortPrefixMultiLineToStringStyle();

    private static final class MinimalToStringStyle extends ToStringStyle {
        MinimalToStringStyle() {
            super();
            this.setUseShortClassName(true);
            this.setUseIdentityHashCode(false);
            this.setUseFieldNames(false);
        }
    }

    private static final class ShortPrefixMultiLineToStringStyle extends ToStringStyle {
        ShortPrefixMultiLineToStringStyle() {
            super();
            this.setUseShortClassName(true);
            this.setUseIdentityHashCode(false);
            this.setFieldSeparatorAtStart(true);
            this.setFieldSeparator("\n     ");
            this.setContentEnd("\n]");
        }
    }
}
package com.thoughtworks.core.web;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.collect.Lists.newArrayList;

@Component
public class TargetUrl {
    public String getFullUrl(){
        return "http://www.google.com";
    }
}

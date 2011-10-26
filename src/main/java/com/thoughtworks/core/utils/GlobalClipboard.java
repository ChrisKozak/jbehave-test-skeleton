package com.thoughtworks.core.utils;

import org.springframework.stereotype.Component;

import java.util.List;

import static com.thoughtworks.core.utils.CustomToStringStyle.SHORT_PREFIX_MULTI_LINE_STYLE;
import static com.thoughtworks.core.utils.ReflectionTargetedToStringBuilder.reflectionTargetedToString;

@Component
public class GlobalClipboard {
    private String mostRecentSearchTerm;
    private List<String> mostRecentSearchResults;

    @Override
    public String toString() {
        return reflectionTargetedToString(this, SHORT_PREFIX_MULTI_LINE_STYLE);
    }

    public void rememberLastSearchTerm(String searchTerm) {
        this.mostRecentSearchTerm = searchTerm;
    }

    public void rememberLastSearchResults(List<String> searchResults) {
        this.mostRecentSearchResults = searchResults;
    }

    public List<String> getLastSearchResults() {
        return mostRecentSearchResults;
    }
}

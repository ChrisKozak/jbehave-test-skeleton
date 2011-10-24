package com.thoughtworks.core.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang.builder.HashCodeBuilder.reflectionHashCode;

public class StepInfo {

    private String name;
    private String scenario;
    private Map<String, List<String>> storyFailures;

    public StepInfo(String name, String scenario) {
        this.name = name;
        this.scenario = scenario;
        storyFailures = new HashMap<String, List<String>>();
    }

    public void updateStoryFailures(String storyName, String failureCause) {
        if (!storyFailures.containsKey(storyName)) {
            storyFailures.put(storyName, new ArrayList());
        }
        storyFailures.get(storyName).add(failureCause);
    }

    public Map<String, List<String>> getStoryFailures() {
        return storyFailures;

    }

    public String getName() {
        return null == name ? "" : name;
    }

    public int getFailuresCount() {
        return storyFailures.size();
    }


    @Override
    public boolean equals(Object o) {
        return this.name.equals(((StepInfo) o).getName());
    }

    @Override
    public int hashCode() {
        return reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return "'" + name + "', failures: " + getFailuresCount();
    }

    public String getScenario() {
        return scenario;
    }
}

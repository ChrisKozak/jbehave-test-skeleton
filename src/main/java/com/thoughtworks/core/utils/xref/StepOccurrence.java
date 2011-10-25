package com.thoughtworks.core.utils.xref;

import org.jbehave.core.model.Meta;
import org.jbehave.core.model.Scenario;
import org.jbehave.core.model.Story;

import static com.thoughtworks.core.utils.xref.CustomXRefStory.KNOWN_ISSUE;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.substringBefore;

class StepOccurrence {
    private String story;
    private String scenario;
    private String step;
    private Outcome outcome;
    private Boolean isKnownIssue = false;

    public StepOccurrence(Story story, Scenario scenario, String step, Outcome outcome) {
        this.story = story.getPath();
        this.scenario = scenario.getTitle();
        this.step = step;
        this.outcome = outcome;
        this.isKnownIssue = knownIssue(story.getMeta());
    }

    private boolean knownIssue(Meta meta) {
        return meta.hasProperty(KNOWN_ISSUE);
    }

    private boolean knownIssueForAllEnvironments(String propertyValue) {
        return isBlank(substringBefore(propertyValue, "-"));
    }

    public static enum Outcome {
        NOT_PERFORMED, PASSED, PENDING, FAILED, IGNORED;

    }
}

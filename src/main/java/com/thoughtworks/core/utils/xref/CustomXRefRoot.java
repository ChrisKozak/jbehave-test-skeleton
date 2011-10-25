package com.thoughtworks.core.utils.xref;

import com.thoughtworks.core.utils.AssertionLog;
import org.jbehave.core.model.Story;
import org.jbehave.core.reporters.CrossReference;
import org.jbehave.core.reporters.StoryReporterBuilder;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

// Understands how to represent the data source to be used by the story navigator
public class CustomXRefRoot extends CrossReference.XRefRoot {
    private Set<String> metaTags = new HashSet<String>();
    private List<XrefStep> stepOccurrences;

    private transient AssertionLog assertionLog;
    private transient Map<Story, List<ScenarioResult>> scenarioResults;

    public CustomXRefRoot(AssertionLog assertionLog, Map<Story, List<ScenarioResult>> scenarioResults, List<XrefStep> xrefSteps) {
        this.assertionLog = assertionLog;
        this.scenarioResults = scenarioResults;
        this.stepOccurrences = xrefSteps;
    }

    @Override
    protected String createdBy() {
        return "FTA";
    }

    protected CrossReference.XRefStory createXRefStory(StoryReporterBuilder storyReporterBuilder, Story story, boolean passed) {
        return new CustomXRefStory(story, storyReporterBuilder, passed, scenarioResults.get(story));
    }

    public void addMetaOption(String option) {
        metaTags.add(option);
    }
}

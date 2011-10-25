package com.thoughtworks.core.utils.xref;

import org.jbehave.core.model.Meta;
import org.jbehave.core.model.Story;
import org.jbehave.core.reporters.CrossReference;
import org.jbehave.core.reporters.StoryReporterBuilder;

import java.util.List;

import static java.lang.Boolean.FALSE;
import static org.apache.commons.lang.StringUtils.*;

// Understands how to represent the story in the data source used by the story navigator
public class CustomXRefStory extends CrossReference.XRefStory {
    protected static final String KNOWN_ISSUE = "KnownIssue";

    transient private Story story;
    private String knownIssue = "";
    private Boolean hasKnownIssue = FALSE;
    private List<ScenarioResult> scenarioResults;
    private String readableName = null;

    public CustomXRefStory(Story story, StoryReporterBuilder storyReporterBuilder, boolean passed, List<ScenarioResult> scenarioResults) {
        super(story, storyReporterBuilder, passed);
        this.story = story;
        this.scenarioResults = scenarioResults;
        this.readableName = convertToHumanReadableName(story.getName());

        if (knownIssue(story.getMeta())) {
            hasKnownIssue = true;
            knownIssue = story.getMeta().getProperty(KNOWN_ISSUE);
        }
    }


    private String convertToHumanReadableName(String name) {
        return capitalize(name.replaceAll(".story", "").replaceAll("_", " "));
    }

    private boolean knownIssue(Meta meta) {
        return meta.hasProperty(KNOWN_ISSUE);
    }

    @Override
    protected void processMetaTags(CrossReference.XRefRoot root) {
        // This is needed to make sure that the meta property gets set for each story.
        // We ignore the changes to the meta field in the XRefRoot class.
        super.processMetaTags(root);

        populateRootMetaTags((CustomXRefRoot) root);
    }

    private void populateRootMetaTags(CustomXRefRoot root) {
        for (String next : story.getMeta().getPropertyNames()) {
            root.addMetaOption(next + "=" + story.getMeta().getProperty(next));
        }
    }

}

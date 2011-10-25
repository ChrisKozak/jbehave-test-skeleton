package com.thoughtworks.core;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.jbehave.core.model.*;
import org.jbehave.core.reporters.FilePrintStreamFactory;
import org.jbehave.core.reporters.Format;
import org.jbehave.core.reporters.StoryReporter;
import org.jbehave.core.reporters.StoryReporterBuilder;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static java.lang.System.currentTimeMillis;
import static java.util.Arrays.asList;

public class CustomPostStoryStatisticsCollector implements StoryReporter {

    protected static final String KNOWN_ISSUE = "KnownIssue";

    private final OutputStream output;
    private final Map<String, Integer> data = new HashMap<String, Integer>();
    private final List<String> events = asList("notAllowed", "pending", "scenariosNotAllowed",
            "givenStoryScenariosNotAllowed", "steps", "stepsSuccessful", "stepsIgnorable", "stepsPending",
            "stepsNotPerformed", "stepsFailed", "currentScenarioSteps", "currentScenarioStepsPending", "scenarios",
            "scenariosSuccessful", "scenariosPending", "scenariosFailed", "givenStories", "givenStoryScenarios",
            "givenStoryScenariosSuccessful", "givenStoryScenariosPending", "givenStoryScenariosFailed", "examples", "knownIssue");

    private Throwable cause;
    private OutcomesTable outcomesFailed;
    private int givenStories;
    private long storyStartTime;

    public static Format customStats() {
        return new Format("STATS") {
            @Override
            public StoryReporter createStoryReporter(FilePrintStreamFactory factory, StoryReporterBuilder storyReporterBuilder) {
                factory.useConfiguration(storyReporterBuilder.fileConfiguration("stats"));
                return new CustomPostStoryStatisticsCollector(factory.createPrintStream());
            }
        };
    }

    public CustomPostStoryStatisticsCollector(OutputStream output) {
        this.output = output;
    }

    public void successful(String step) {
        add("steps");
        add("stepsSuccessful");
        add("currentScenarioSteps");
    }

    public void ignorable(String step) {
        add("steps");
        add("stepsIgnorable");
        add("currentScenarioSteps");
    }

    public void pending(String step) {
        add("steps");
        add("stepsPending");
        add("currentScenarioSteps");
        add("currentScenarioStepsPending");
    }

    public void notPerformed(String step) {
        add("steps");
        add("stepsNotPerformed");
        add("currentScenarioSteps");
    }

    public void failed(String step, Throwable cause) {
        this.cause = cause;
        add("steps");
        add("stepsFailed");
        add("currentScenarioSteps");
    }

    public void failedOutcomes(String step, OutcomesTable table) {
        this.outcomesFailed = table;
        add("steps");
        add("stepsFailed");
        add("currentScenarioSteps");
    }

    public void beforeStory(Story story, boolean givenStory) {
        if (givenStory) {
            this.givenStories++;
        }

        if (!givenStory) {
            resetData();
            storyStartTime = currentTimeMillis();
        }

        if (knownIssue(story.getMeta())) {
            add("knownIssue");
        }
    }

    private boolean knownIssue(Meta meta) {
        return meta.hasProperty(KNOWN_ISSUE);
    }

    public void narrative(final Narrative narrative) {
    }

    public void storyNotAllowed(Story story, String filter) {
        resetData();
        add("notAllowed");
        writeData();
    }

    public void afterStory(boolean givenStory) {
        if (givenStory) {
            this.givenStories--;
        } else {
            if (has("scenariosPending") || has("givenStoryScenariosPending")) {
                add("pending");
            }
            int duration = (int) (currentTimeMillis() - storyStartTime);
            data.put("duration", duration);
            writeData();
        }
    }

    public void givenStories(GivenStories givenStories) {
        add("givenStories");
    }

    public void givenStories(List<String> storyPaths) {
        add("givenStories");
    }

    public void beforeScenario(String title) {
        cause = null;
        outcomesFailed = null;
        reset("currentScenarioSteps");
        reset("currentScenarioStepsPending");
    }

    public void scenarioNotAllowed(Scenario scenario, String filter) {
        if (givenStories > 0) {
            add("givenStoryScenariosNotAllowed");
        } else {
            add("scenariosNotAllowed");
        }
    }

    public void scenarioMeta(Meta meta) {
    }

    public void afterScenario() {
        if (givenStories > 0) {
            countScenarios("givenStoryScenarios");
        } else {
            countScenarios("scenarios");
        }
        if (has("currentScenarioStepsPending") || !has("currentScenarioSteps")) {
            if (givenStories > 0) {
                add("givenStoryScenariosPending");
            } else {
                add("scenariosPending");
            }
        }
    }

    private void countScenarios(String namespace) {
        add(namespace);
        if (cause != null || outcomesFailed != null) {
            add(namespace + "Failed");
        } else {
            add(namespace + "Successful");
        }
    }

    public void beforeExamples(List<String> steps, ExamplesTable table) {
    }

    public void example(Map<String, String> tableRow) {
        add("examples");
    }

    public void afterExamples() {
    }

    public void dryRun() {
    }

    public void pendingMethods(List<String> methods) {
    }

    private void add(String event) {
        Integer count = data.get(event);
        if (count == null) {
            count = 0;
        }
        count++;
        data.put(event, count);
    }

    private boolean has(String event) {
        Integer count = data.get(event);
        if (count == null) {
            count = 0;
        }
        return count > 0;
    }

    private void writeData() {
        Properties p = new Properties();
        for (String event : data.keySet()) {
            if (!event.startsWith("current")) {
                p.setProperty(event, data.get(event).toString());
            }
        }
        try {
            p.store(output, this.getClass().getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void resetData() {
        data.clear();
        for (String event : events) {
            reset(event);
        }
    }

    private void reset(String event) {
        data.put(event, 0);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append(output).append(data).toString();
    }

}


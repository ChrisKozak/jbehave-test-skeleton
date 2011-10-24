package com.thoughtworks.core;

import com.thoughtworks.core.steps.StepContainer;
import com.thoughtworks.core.utils.AssertionLog;
import com.thoughtworks.core.utils.StackTraceAssertionListener;
import org.jbehave.core.Embeddable;
import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.configuration.MostUsefulConfiguration;
import org.jbehave.core.embedder.*;
import org.jbehave.core.failures.FailingUponPendingStep;
import org.jbehave.core.io.CodeLocations;
import org.jbehave.core.io.LoadFromClasspath;
import org.jbehave.core.io.StoryFinder;
import org.jbehave.core.junit.JUnitStories;
import org.jbehave.core.reporters.CustomHtmlFormat;
import org.jbehave.core.reporters.Format;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.jbehave.core.steps.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.lang.Boolean.getBoolean;
import static java.util.Arrays.asList;

public abstract class GlobalStories extends JUnitStories {
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    protected AssertionLog assertionLog;

    private List<String> storyFinders;

    public static final String JBEHAVE_REPORT_DIR = "jbehave.report.dir";
    public static final String JBEHAVE_CURRENT_STORY_NAME = "jbehave.current.story";
    public static final String STORIES_DIR = "stories.dir";

    private static final Date NOW = new Date();

    public GlobalStories(List<String> storyFinders, String metafilters) {
        this(storyFinders, metafilters, new CoreStoryRunner());
    }

    public GlobalStories(List<String> storyFinders, String metafilters, StoryRunner storyRunner) {
        super();
        this.storyFinders = storyFinders;

        Embedder embedder = new Embedder(new StoryMapper(), storyRunner, new CustomEmbedderMonitor());
        embedder.useEmbedderControls(new EmbedderControls().doIgnoreFailureInStories(true));
        embedder.useMetaFilters(asList("-Ignore * -DataMissing * " + metafilters));
        useEmbedder(embedder);
    }

    public GlobalStories(List<String> storyFinders) {
        this(storyFinders, "");
    }

    @Override
    public Configuration configuration() {
        Class<? extends Embeddable> embeddableClass = this.getClass();

        assertionLog.addAssertionListeners(new StackTraceAssertionListener());

        Properties properties = new Properties();
        properties.setProperty("reports", "reports/results-table.ftl");
        properties.setProperty("views", "reports/index-view.ftl");
        StoryReporterBuilder reporterBuilder = new StoryReporterBuilder()
                .withCodeLocation(CodeLocations.codeLocationFromClass(embeddableClass))
                .withDefaultFormats()
                .withFailureTrace(true)
                .withViewResources(properties)
                .withRelativeDirectory("jbehave" + directorySuffix())
                // withFormats processes the formats based on Alphabetical order and not on who is passed first. TODO create afterStep to improve it
                .withFormats(Format.CONSOLE, Format.XML, CustomHtmlFormat.customHtml(assertionLog));

        System.setProperty(JBEHAVE_REPORT_DIR, reporterBuilder.outputDirectory().toString());
        String storiesPath = null;
        try {
            storiesPath = new File(getClass().getClassLoader().getResource("stories").toURI()).getParentFile().getAbsolutePath();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        System.setProperty(STORIES_DIR, storiesPath);

        Configuration configuration = new MostUsefulConfiguration()
                .useParameterConverters(new ParameterConverters().addConverters(new ParameterConverters.StringListConverter("~")))
                .useFailureStrategy(new FailingUponPendingStep())
                .useStoryLoader(new LoadFromClasspath(embeddableClass))
                .useStoryControls(new StoryControls().doSkipScenariosAfterFailure(false))
                .useStoryReporterBuilder(reporterBuilder)
                .useStepFinder(new StepFinder(new ReversedPrioritisingStrategy()));

        return configuration;
    }

    private String directorySuffix() {
        String datePattern = "MM-dd-yy_H-mm-ss";
        SimpleDateFormat dateFormatter = new SimpleDateFormat(datePattern);
        if (getBoolean("continuous.integration.box")) {
            return "";
        }
        return "-" + dateFormatter.format(NOW);
    }

    @Override
    public List<CandidateSteps> candidateSteps() {
        return getSteps().candidates(configuration());
    }

    @Override
    protected List<String> storyPaths() {
        return new StoryFinder().findPaths(System.getProperty(STORIES_DIR), storyFinders, null);
    }

    public StepContainer getSteps() {
        return applicationContext.getBean("stepContainer", StepContainer.class);
    }

    private class ReversedPrioritisingStrategy implements StepFinder.PrioritisingStrategy {
        public List<StepCandidate> prioritise(String stepAsString, List<StepCandidate> candidates) {
            Collections.sort(candidates, new Comparator<StepCandidate>() {
                public int compare(StepCandidate o1, StepCandidate o2) {
                    // MTC + AK: this is reversing the default priority mechanism.
                    // We did this to pick up the steps defined in the children classes before those defined in the parent classes.
                    return o1.getPriority().compareTo(o2.getPriority());
                }
            });
            return candidates;
        }
    }
}

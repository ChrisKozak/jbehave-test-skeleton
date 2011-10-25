package com.thoughtworks.core.utils.xref;

import com.thoughtworks.core.steps.ScenarioFailedFromAssertionFailuresException;
import com.thoughtworks.core.utils.AssertionLog;
import org.jbehave.core.failures.UUIDExceptionWrapper;
import org.jbehave.core.model.Scenario;
import org.jbehave.core.model.Story;
import org.jbehave.core.reporters.NullStoryReporter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.CoreMatchers.is;

public class XRefStoryReporter extends NullStoryReporter {
    private Map<Story, List<ScenarioResult>> scenarioResults = new HashMap<Story, List<ScenarioResult>>();
    private List<XrefStep> xrefSteps = new ArrayList<XrefStep>();
    private Story currentStory;
    private Scenario currentScenario;
    private Throwable currentFailure;
    private AssertionLog assertionLog;
    private Integer assertionFailureCount = 0;
    private static final String STRANGE_OPEN_PARENS = "｟";
    private static final String STRANGE_CLOSE_PARENS = "｠";
    private String lastStepType;

    public XRefStoryReporter(AssertionLog assertionLog) {
        this.assertionLog = assertionLog;
    }

    @Override
    public void beforeStory(Story story, boolean givenStory) {
        currentStory = story;
        scenarioResults.put(currentStory, new ArrayList<ScenarioResult>());
    }

    @Override
    public void beforeScenario(String scenarioTitle) {
        currentScenario = selectFirst(currentStory.getScenarios(), having(on(Scenario.class).getTitle(), is(scenarioTitle)));
        currentFailure = null;
        assertionFailureCount = 0;
    }

    @Override
    public void successful(String step) {
        if (assertionLog.failureCount() > assertionFailureCount) {
            handleAssertFailure(step);
            return;
        }
        addOccurrenceFor(step, StepOccurrence.Outcome.PASSED);
    }

    private void handleAssertFailure(String step) {
        failed(step, new UUIDExceptionWrapper(assertionLog.getLastAssertionFailure().getFailure()));
        assertionFailureCount++;
    }

    @Override
    public void ignorable(String step) {
        addOccurrenceFor(step, StepOccurrence.Outcome.IGNORED);
    }

    @Override
    public void pending(String step) {
        addOccurrenceFor(step, StepOccurrence.Outcome.PENDING);
    }

    @Override
    public void notPerformed(String step) {
        addOccurrenceFor(step, StepOccurrence.Outcome.NOT_PERFORMED);
    }

    @Override
    public void failed(String step, Throwable cause) {
        if (cause.getCause().getCause() instanceof ScenarioFailedFromAssertionFailuresException) {
            return;
        }

        addOccurrenceFor(step, StepOccurrence.Outcome.FAILED);
        currentFailure = cause;
    }

    @Override
    public void afterScenario() {
        scenarioResults.get(currentStory).add(new ScenarioResult(currentScenario.getTitle(), extractFailureMessage(), currentFailure == null));
    }

    private String extractFailureMessage() {
        if (currentFailure != null) {
            if (currentFailure.getCause() != null) {
                return currentFailure.getCause().getMessage();
            }
            return currentFailure.getMessage();
        }
        return null;
    }

    private void addOccurrenceFor(String step, StepOccurrence.Outcome outcome) {
        xrefFor(step).addOccurrence(new StepOccurrence(currentStory, currentScenario, step, outcome));
    }

    private XrefStep xrefFor(String stepString) {
        String[] tokens = stepString.split(" ");
        String stepType = tokens[0].toUpperCase();
        String stepText = stepString.substring(stepString.indexOf(" ") + 1);
        stepText = stepText.replaceAll(STRANGE_OPEN_PARENS + ".+" + STRANGE_CLOSE_PARENS, "X");
        if (stepType.equalsIgnoreCase("and")) {
            stepType = lastStepType;
        } else {
            lastStepType = stepType;
        }
        XrefStep xrefStep = new XrefStep(lastStepType, stepText);
        return getOrCreate(xrefStep);
    }

    private XrefStep getOrCreate(XrefStep newXrefStep) {
        for (XrefStep xrefStep : xrefSteps) {
            if (xrefStep.getType().equals(newXrefStep.getType()) && xrefStep.getAnnotatedPattern().equals(newXrefStep.getAnnotatedPattern()))
                return xrefStep;
        }
        xrefSteps.add(newXrefStep);
        return newXrefStep;
    }

    public List<XrefStep> getXrefSteps() {
        return xrefSteps;
    }

    public Map<Story, List<ScenarioResult>> getScenarioResults() {
        return scenarioResults;
    }
}

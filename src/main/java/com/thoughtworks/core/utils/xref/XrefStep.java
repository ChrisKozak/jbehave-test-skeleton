package com.thoughtworks.core.utils.xref;

import java.util.ArrayList;
import java.util.List;

public class XrefStep {

    private String type;
    private String annotatedPattern; // TODO: MATT - this is not the annotated pattern (not really -> we should use a XrefStepMonitor to get the real annotated pattern)
    private List<StepOccurrence> occurrences = new ArrayList<StepOccurrence>();

    public XrefStep(String stepType, String annotatedPattern) {
        this.type = stepType;
        this.annotatedPattern = annotatedPattern;
    }

    public void addOccurrence(StepOccurrence occurrence) {
        occurrences.add(occurrence);
    }

    public String getType() {
        return type;
    }

    public String getAnnotatedPattern() {
        return annotatedPattern;
    }

}

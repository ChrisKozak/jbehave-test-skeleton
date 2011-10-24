package com.thoughtworks.core.steps;

import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.steps.CandidateSteps;
import org.jbehave.core.steps.InstanceStepsFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class StepContainer {

    @Autowired
    private ApplicationContext context;

    public List<Object> getSteps() {
        List<Object> allBeans = new ArrayList<Object>(context.getBeansOfType(Object.class).values());
        return allBeans;
    }

    public List<CandidateSteps> candidates(Configuration configuration) {
        return new InstanceStepsFactory(configuration, getSteps())
                .createCandidateSteps();
    }
}

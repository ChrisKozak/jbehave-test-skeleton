package com.thoughtworks.core.utils.xref;

import com.thoughtworks.core.utils.AssertionLog;
import com.thoughtworks.xstream.XStream;
import org.jbehave.core.model.Scenario;
import org.jbehave.core.reporters.CrossReference;

public class CustomCrossReference extends CrossReference {

    private String metaFilters;
    private AssertionLog assertionLog;
    private XRefStoryReporter xrefStoryReporter;

    public CustomCrossReference(AssertionLog assertionLog, XRefStoryReporter xrefStoryReporter) {
        this.assertionLog = assertionLog;
        this.xrefStoryReporter = xrefStoryReporter;
    }

    protected void aliasForXRefStory(XStream xstream) {
        xstream.alias("story", CustomXRefStory.class);
    }

    protected void aliasForXRefRoot(XStream xstream) {
        xstream.alias("xref", CustomXRefRoot.class);
        ignoreStepMatchesFromXrefRootNowThatWeAreUsingACustomXrefRoot(xstream);
        ignoreUnwantedFieldOnScenarioClass(xstream);
    }

    private void ignoreUnwantedFieldOnScenarioClass(XStream xstream) {
        xstream.omitField(Scenario.class, "examplesTable");
        xstream.omitField(Scenario.class, "givenStories");
        xstream.omitField(Scenario.class, "meta");
        xstream.omitField(XRefStory.class, "name");
    }

    private void ignoreStepMatchesFromXrefRootNowThatWeAreUsingACustomXrefRoot(XStream xstream) {
        xstream.omitField(XRefRoot.class, "stepMatches");
    }

    @Override
    protected XRefRoot newXRefRoot() {
        return new CustomXRefRoot(assertionLog, xrefStoryReporter.getScenarioResults(), xrefStoryReporter.getXrefSteps());
    }

    public CrossReference withMetaFilters(String metaFilters) {
        this.metaFilters = metaFilters;
        return this;
    }

    @Override
    public String getMetaFilter() {
        return metaFilters;
    }

}

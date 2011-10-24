package org.jbehave.core.reporters;

import com.thoughtworks.core.steps.ScenarioFailedFromAssertionFailuresException;
import com.thoughtworks.core.utils.AssertionListener;
import com.thoughtworks.core.utils.AssertionLog;
import com.thoughtworks.core.utils.SoftAssertion;
import org.jbehave.core.configuration.Keywords;
import org.jbehave.core.failures.UUIDExceptionWrapper;
import org.jbehave.core.model.Story;

import java.io.PrintStream;
import java.util.List;
import java.util.Properties;

import static ch.lambdaj.Lambda.*;
import static org.jbehave.core.reporters.PrintStreamOutput.Format.HTML;

public class CustomHtmlFormat {

    public static final Format customHtml(final AssertionLog assertionLog) {
        return new Format("html") {
            @Override
            public StoryReporter createStoryReporter(FilePrintStreamFactory factory, StoryReporterBuilder storyReporterBuilder) {
                factory.useConfiguration(storyReporterBuilder.fileConfiguration("html"));
                return new CustomHtml(factory.createPrintStream(), storyReporterBuilder.keywords(), assertionLog).doReportFailureTrace(storyReporterBuilder.reportFailureTrace());
            }
        };
    }

    private static class CustomHtml extends PrintStreamOutput {
        private Properties outputPatterns;
        private String storyName;
        private AssertionLog assertionLog;

        public CustomHtml(PrintStream output, Keywords keywords, AssertionLog assertionLog) {
            super(HTML, output, new Properties(), keywords, false, false);
            this.assertionLog = assertionLog;
            outputPatterns = patterns();
        }

        @Override
        public void successful(String step) {
            if (assertionLog.hasAnyNewFailures()) {
                printToReport(step, new UUIDExceptionWrapper(assertionLog.firstNewFailedAssertion().getFailure()));
            } else {
                printToReport(step, null);
            }
        }

        @Override
        public void failed(String step, Throwable failure) {
            if (failure.getCause().getCause() instanceof ScenarioFailedFromAssertionFailuresException) {
                return;
            }

            printToReport(step, failure);
        }

        @Override
        public void afterScenario() {
            super.afterScenario();
            print(buildHtmlOutputAfterScenario());

        }

        private void printToReport(String step, Throwable failure) {
            print("<div class='stepWrapper'>");

            if (failure == null) {
                super.successful(step);

            } else {
                super.failed(step, failure);
                customFailed(step, failure);
            }

            print("<div class='assertionListeners'>");

            printAssertionsToReport();
            if (failure != null) {
                print(buildHtmlOutputFromAssertionListeners(step, failure));
            }

            print("</div>");

            print("</div>");
        }

        private void customFailed(String step, Throwable failure) {
            print(format("customFailed", "{0} ({1})\n({2})\n", step, failure.getCause().getMessage()));
        }

        private String buildHtmlOutputAfterScenario() {
            List<String> htmlBits = extract(assertionLog.getListeners(), on(AssertionListener.class).afterScenario(storyName));
            return join(htmlBits, "\n");
        }

        private String buildHtmlOutputFromAssertionListeners(String step, Throwable failure) {
             List<String> htmlBits = extract(assertionLog.getListeners(), on(AssertionListener.class).afterStepFailure(assertionLog, storyName, step, failure));
            return join(htmlBits, "\n");
        }

        private void printAssertionsToReport() {
            for (SoftAssertion assertion : assertionLog.getNewAssertions()) {
                print(format(assertion.passed() ? "assertions" : "failedAssertions", null, assertion));
            }
            assertionLog.markAssertionsAsSeen();

        }

        public void beforeStory(Story story, boolean givenStory) {
            this.storyName = story.getName();
            super.beforeStory(story, givenStory);
        }

        protected String lookupPattern(String key, String defaultPattern) {
            if (outputPatterns.containsKey(key)) {
                return outputPatterns.getProperty(key);
            }
            return defaultPattern;
        }

        private static Properties patterns() {
            Properties patterns = new Properties();
            patterns.setProperty("dryRun", "<div class=\"dryRun\">{0}</div>\n");
            patterns.setProperty("assertions", "<div class=\"step successfulAssertion\">{0}</div>\n");
            patterns.setProperty("failedAssertions", "<div class=\"step failed\">{0}</div>\n");
            patterns.setProperty("beforeStory", "<div class=\"story\">\n<h1>{0}</h1>\n<div class=\"path\">{1}</div>\n");
            patterns.setProperty("afterStory", "</div>\n");
            patterns.setProperty("metaStart", "<div class=\"meta\">\n<div class=\"keyword\">{0}</div>\n");
            patterns.setProperty("metaProperty", "<div class=\"property\">{0}{1} {2}</div>\n");
            patterns.setProperty("metaEnd", "</div>\n<div class=\"actions\"><a href=\"#\" onclick=\"for each (var item in document.getElementsByClassName(''successfulAssertion'')) item.style.display = item.style.display == ''none'' || item.style.display == '''' ? ''block'' : ''none''; return false;\">Toggle successful assertions</a></div>\n");
            patterns.setProperty("filter", "<div class=\"filter\">{0}</div>\n");
            patterns.setProperty("narrative", "<div class=\"narrative\"><h2>{0}</h2>\n<div class=\"element inOrderTo\"><span class=\"keyword inOrderTo\">{1}</span> {2}</div>\n<div class=\"element asA\"><span class=\"keyword asA\">{3}</span> {4}</div>\n<div class=\"element iWantTo\"><span class=\"keyword iWantTo\">{5}</span> {6}</div>\n</div>\n");
            patterns.setProperty("beforeScenario", "<div class=\"scenario\">\n<h2>{0} {1}</h2>\n");
            patterns.setProperty("afterScenario", "</div>\n");
            patterns.setProperty("afterScenarioWithFailure", "</div>\n");
            patterns.setProperty("givenStories", "<div class=\"givenStories\">{0} {1}</div>\n");
            patterns.setProperty("givenStoriesStart", "<div class=\"givenStories\">{0}\n");
            patterns.setProperty("givenStory", "<div class=\"givenStory\">{0} {1}</div>\n");
            patterns.setProperty("givenStoriesEnd", "</div>\n");
            patterns.setProperty("successful", "<div class=\"step successful\">{0}</div>\n");
            patterns.setProperty("ignorable", "<div class=\"step ignorable\">{0}</div>\n");
            patterns.setProperty("pending", "<div class=\"step pending\">{0} <span class=\"keyword pending\">({1})</span></div>\n");
            patterns.setProperty("notPerformed", "<div class=\"step notPerformed\">{0} <span class=\"keyword notPerformed\">({1})</span></div>\n");
            patterns.setProperty("customFailed", "<div class=\"step failed\">{0} <span class=\"keyword failed\"> -- FAILED: {1}</span></div>\n");
            patterns.setProperty("failed", "");
            patterns.setProperty("outcomesTableStart", "<div class=\"outcomes\"><table>\n");
            patterns.setProperty("outcomesTableHeadStart", "<thead>\n<tr>\n");
            patterns.setProperty("outcomesTableHeadCell", "<th>{0}</th>");
            patterns.setProperty("outcomesTableHeadEnd", "</tr>\n</thead>\n");
            patterns.setProperty("outcomesTableBodyStart", "<tbody>\n");
            patterns.setProperty("outcomesTableRowStart", "<tr class=\"{0}\">\n");
            patterns.setProperty("outcomesTableCell", "<td>{0}</td>");
            patterns.setProperty("outcomesTableRowEnd", "</tr>\n");
            patterns.setProperty("outcomesTableBodyEnd", "</tbody>\n");
            patterns.setProperty("outcomesTableEnd", "</table></div>\n");
            patterns.setProperty("beforeExamples", "<div class=\"examples\">\n<h3>{0}</h3>\n");
            patterns.setProperty("examplesStep", "<div class=\"step\">{0}</div>\n");
            patterns.setProperty("afterExamples", "</div>\n");
            patterns.setProperty("examplesTableStart", "<table>\n");
            patterns.setProperty("examplesTableHeadStart", "<thead>\n<tr>\n");
            patterns.setProperty("examplesTableHeadCell", "<th>{0}</th>");
            patterns.setProperty("examplesTableHeadEnd", "</tr>\n</thead>\n");
            patterns.setProperty("examplesTableBodyStart", "<tbody>\n");
            patterns.setProperty("examplesTableRowStart", "<tr>\n");
            patterns.setProperty("examplesTableCell", "<td>{0}</td>");
            patterns.setProperty("examplesTableRowEnd", "</tr>\n");
            patterns.setProperty("examplesTableBodyEnd", "</tbody>\n");
            patterns.setProperty("examplesTableEnd", "</table>\n");
            patterns.setProperty("example", "\n<h3 class=\"example\">{0} {1}</h3>\n");
            patterns.setProperty("parameterValueStart", "<span class=\"step parameter\">");
            patterns.setProperty("parameterValueEnd", "</span>");
            patterns.setProperty("parameterValueNewline", "<br/>");
            return patterns;
        }
    }
}

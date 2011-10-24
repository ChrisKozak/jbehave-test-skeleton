package com.thoughtworks.core.utils;

import java.io.File;
import java.util.UUID;

// Executes some task after an assertion fails
public abstract class AssertionListener implements Comparable {
    private UUID uuid;

    /**
     *  Called by the story reporter after a step has failed
     */
    final public String afterStepFailure(AssertionLog assertionLog, String storyName, String step, Throwable failure){
        String returnValue = afterStepFailed(assertionLog,storyName, step, failure);
        resetAfterStep();
        return returnValue;
    }

    /**
     * Implement this method in the children to output some HTML to the report after each scenario.
     * @returns a String containing HTML
     * @param storyName
     */
    public String afterScenario(String storyName) {
        return "";
    }

    /**
     * Implement this method in the children to output some HTML to the final report after any step failure
     * @returns a String containing HTML
     */
    protected String afterStepFailed(AssertionLog assertionLog, String storyName, String step, Throwable failure) {
        return "";
    }

    /**
     * Implement this method in the children to execute some task right after any assertion failed
     */
    public void afterAssertFailed() {}

    /**
     * @returns the index of the listener in the report (a low index means the listener will be called before the others)
     */
    public Integer getIndex() {return 5;}

    protected UUID resetUUID() {
        uuid = UUID.randomUUID();
        return uuid;
    }

    protected UUID currentUUID() {
        return uuid;
    }

    public void resetAfterStep(){
        uuid = null;
    }

    protected static File getUniqueFile(Object uid, String fileType) {
        return new File(CoreUtils.resolveDirectory("resources"), System.getProperty("jbehave.current.story") + uid + "." + fileType);
    }

    @Override
    public int compareTo(Object other) {
        return getIndex().compareTo(((AssertionListener) other).getIndex());
    }
}

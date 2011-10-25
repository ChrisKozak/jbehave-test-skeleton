package com.thoughtworks.core.utils.xref;

class ScenarioResult {
    private String title;
    private String failure;
    private Boolean passed;

    ScenarioResult(String title, String failure, Boolean passed) {
        this.title = title;
        this.failure = failure;
        this.passed = passed;
    }
}

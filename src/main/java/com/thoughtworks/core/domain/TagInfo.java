package com.thoughtworks.core.domain;

public class TagInfo {

    private String tag;
    private String description;
    private String storyName;
    private String issue;

    public TagInfo() {}

    public TagInfo(String tag, String description, String storyName, String issue) {
        this.tag = tag;
        this.description = description;
        this.storyName = storyName;
        this.issue = issue;
    }

    @Override
    public String toString() {
        return  "@" + tag + " " + description + " -- " + storyName;
    }

    public String getTag() {
        return tag;
    }

    public String getDescription() {
        return description;
    }

    public String getStoryName() {
        return storyName;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStoryName(String storyName) {
        this.storyName = storyName;
    }
}

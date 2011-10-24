package com.thoughtworks.core;

import org.jbehave.core.embedder.MetaFilter;
import org.jbehave.core.embedder.PrintStreamEmbedderMonitor;
import org.jbehave.core.model.Meta;

public class CustomEmbedderMonitor extends PrintStreamEmbedderMonitor {

    @Override
    public void metaNotAllowed(Meta meta, MetaFilter filter) {
        // print nothing for stories that are not allowed
    }

    @Override
    public void runningStory(String path) {
        // do not print the name of the story -- it is already printed out once somewhere else
    }
}

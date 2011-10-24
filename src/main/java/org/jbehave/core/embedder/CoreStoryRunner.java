package org.jbehave.core.embedder;

import com.thoughtworks.core.GlobalStories;
import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.model.Story;
import org.jbehave.core.steps.InjectableStepsFactory;

public class CoreStoryRunner extends StoryRunner {
    @Override
    public void run(Configuration configuration, InjectableStepsFactory stepsFactory, Story story, MetaFilter filter, StoryRunner.State beforeStories) throws Throwable {
        if (!filter.allow(story.getMeta()))
            return;

        System.setProperty(GlobalStories.JBEHAVE_CURRENT_STORY_NAME, story.getName());
        super.run(configuration, stepsFactory, story, filter, beforeStories);
    }
}

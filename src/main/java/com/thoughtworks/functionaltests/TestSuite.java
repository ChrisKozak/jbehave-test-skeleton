package com.thoughtworks.functionaltests;

import com.thoughtworks.core.GlobalStories;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static java.util.Arrays.asList;

@ContextConfiguration("../../../config/core-web-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class TestSuite extends GlobalStories {

    public TestSuite(){
        super(asList("**/google_should_find_me.story"));
    }
}

package com.thoughtworks.core.web;

import com.thoughtworks.core.GlobalStories;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static java.util.Arrays.asList;


@ContextConfiguration(locations = {"/config/core-web-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class VerifyCoreWebIsWorkingTest extends GlobalStories {

    public VerifyCoreWebIsWorkingTest(){
        super(asList("**/verify_list_is_working.story"));
    }
}

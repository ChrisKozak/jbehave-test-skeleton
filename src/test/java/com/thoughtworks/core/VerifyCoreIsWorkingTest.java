package com.thoughtworks.core;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static java.util.Arrays.asList;


@ContextConfiguration("../../../config/core-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class VerifyCoreIsWorkingTest extends GlobalStories {

    public VerifyCoreIsWorkingTest(){
        super(asList("**/verify_list_is_working.story"));
    }
}

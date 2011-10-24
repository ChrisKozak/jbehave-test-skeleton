package com.thoughtworks.core.web;

import org.apache.log4j.Logger;
import org.openqa.selenium.support.ui.Wait;
import org.springframework.beans.factory.annotation.Autowired;

import static com.thoughtworks.core.utils.CoreUtils.getLoggerFor;

public abstract class AbstractComponent {
    protected Logger logger = getLoggerFor(this);

    @Autowired
    protected ExtendedWebDriver extendedWebDriver;
    @Autowired
    protected Wait wait;
}

package com.thoughtworks.core.web;

import com.thoughtworks.core.utils.AssertionLog;
import com.google.common.collect.Lists;
import com.thoughtworks.core.utils.CoreUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.support.ui.Wait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.thoughtworks.core.utils.CoreUtils.getLoggerFor;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.apache.commons.lang.StringUtils.containsIgnoreCase;
import static org.hamcrest.Matchers.is;

// Understands how to interact with the web page elements
@Component
public abstract class AbstractFind {

    protected Logger logger = CoreUtils.getLoggerFor(this);
    @Autowired
    protected ExtendedWebDriver extendedWebDriver;
    @Autowired
    private TargetUrl targetUrl;
    @Autowired
    protected Wait wait;
    @Autowired
    private AssertionLog assertionLog;

    /**
     * This is the preferred way to access the extendedWebDriver so we can hook in some validation.
     *
     * @return the webclient
     */
    public ExtendedWebDriver webClient() {
        String actualPageTitle = pageTitle();
        boolean foundIt = false;
        for (String expectedPageTitle : expectedPageTitles()) {
            if(containsIgnoreCase(actualPageTitle, expectedPageTitle)) {
                foundIt = true;
            }
        }

        assertionLog.ensureQuietlyThat("The displayed page might not be the correct page (we do not recognize the page title)" +
                "\n    Expected: One contained in either of " + expectedPageTitles() +
                "\n    Actual  : " + actualPageTitle, foundIt, is(true));

        return extendedWebDriver;
    }

    public final String pageTitle() {
        return extendedWebDriver.getTitle();
    }

    /**
     * Override this to validate that the title is as expected.
     *
     * @return the title of the page
     */
    protected List<String> expectedPageTitles() {
        return Lists.newArrayList(EMPTY);
    }

    /**
     *
     * @param url Absolute or "site relative" (beginning with context)
     */
    public final void url(String url) {
        webClient().goTo(url);
    }

    public void andClosePopup(){
        webClient().closeAllButTheMainBrowserWindow();
    }

   public void andSwitchToMainBrowserWindow() {
        extendedWebDriver.switchToMainBrowserWindow();
    }

    public void andSwitchToPopupWindow() {
        extendedWebDriver.switchToPopupWindow();
    }
}

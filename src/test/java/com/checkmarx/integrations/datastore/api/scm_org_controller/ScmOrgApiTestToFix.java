package com.checkmarx.integrations.datastore.api.scm_org_controller;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/cucumber/features/scm-org-api.feature",
        plugin = { "pretty", "html:target/cucumber-html-report","json:target/cucumber.json" },
        extraGlue = "com.checkmarx.integrations.datastore.api.shared",
        tags = "not @Skip")
public class ScmOrgApiTestToFix {
}
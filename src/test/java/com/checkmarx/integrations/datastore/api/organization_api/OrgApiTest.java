package com.checkmarx.integrations.datastore.api.organization_api;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/cucumber/features/organization-api",
        plugin = { "pretty", "html:target/cucumber-html-report","json:target/cucumber.json" },
        extraGlue = "com.checkmarx.integrations.datastore.api.shared")
public class OrgApiTest {
}
package com.checkmarx.integrations.datastore.api.tenant_api;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/cucumber/features/tenant-api.feature",
        plugin = { "pretty", "html:target/cucumber-html-report","json:target/cucumber.json" },
        extraGlue = "com.checkmarx.integrations.datastore.api.shared")
public class TenantApiTest {
}

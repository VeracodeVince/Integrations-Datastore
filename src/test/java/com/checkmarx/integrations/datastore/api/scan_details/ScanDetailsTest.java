package com.checkmarx.integrations.datastore.api.scan_details;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/cucumber/features/scan-details-api.feature",
        extraGlue = "com.checkmarx.integrations.datastore.api.shared")
public class ScanDetailsTest {
}

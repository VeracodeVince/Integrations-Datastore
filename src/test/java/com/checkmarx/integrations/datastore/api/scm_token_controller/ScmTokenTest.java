package com.checkmarx.integrations.datastore.api.scm_token_controller;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/cucumber/features/scm-token-api.feature")
public class ScmTokenTest {
}

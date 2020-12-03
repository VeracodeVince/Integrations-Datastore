package com.checkmarx.integrations.datastore.api.scm_repo_controller;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/cucumber/features/scm-repo-api.feature",
        plugin = { "pretty", "html:target/cucumber-html-report","json:target/cucumber.json" },
        tags = "not @Skip")
public class ScmRepoApiTest {
}
package com.checkmarx.integrations.datastore.api.scm_repo_controller;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/cucumber/features/scm-repo-api.feature",
        tags = "not @Skip")
public class ScmRepoApiRunner {
}
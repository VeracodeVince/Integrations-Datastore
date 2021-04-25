package com.checkmarx.integrations.datastore.api.shared;


import com.checkmarx.integrations.datastore.DataStoreApp;
import com.fasterxml.jackson.databind.JsonNode;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RequiredArgsConstructor
public class SharedSteps {
    private final ApiTestState testState;

    private ConfigurableApplicationContext context;
    private final RepoInitializer repoInitializer;

    @Before
    public void before() {
        testState.clear();

        SpringApplication springApplication = new SpringApplication(DataStoreApp.class);
        context = springApplication.run();
    }

    @After
    public void after() {
        context.close();
    }

    @Given("database contains an SCM with ID 1")
    public void dbContainsScm() {
        repoInitializer.createEmptyScms(1);
    }

    @Then("response status is {int}")
    public void responseStatusIs(int expectedStatus) {
        ResponseEntity<JsonNode> response = testState.getLastResponse();

        Assert.assertNotNull("Expected the response to be non-null at this point.", response);
        Assert.assertEquals("Unexpected response status.", expectedStatus, response.getStatusCodeValue());
    }

    @Then("response has a non-empty {string} field")
    public void responseContainsNonEmptyField(String fieldName) {
        JsonNode responseJson = testState.getResponseBodyOrThrow();
        assertTrue(String.format("Response doesn't have the '%s' field.", fieldName), responseJson.has(fieldName));
        String actualValue = responseJson.get(fieldName).asText();
        assertTrue(String.format("The '%s' field must not be empty.", fieldName), StringUtils.isNotEmpty(actualValue));
    }

    @Given("database contains SCMs with IDs:")
    public void dbContainsScms(List<Long> ids) {
        repoInitializer.createEmptyScms(ids.size());
    }

    public static String getEffectiveValue(String valueFromScenario) {
        return valueFromScenario.equals("<null>") ? null : valueFromScenario;
    }

    public static void validateFieldValue(String fieldName, String expected, String actual) {
        expected = SharedSteps.getEffectiveValue(expected);
        assertEquals(String.format("Unexpected value for the %s field.", fieldName), expected, actual);
    }
}
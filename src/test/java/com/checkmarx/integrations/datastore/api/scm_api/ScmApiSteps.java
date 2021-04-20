package com.checkmarx.integrations.datastore.api.scm_api;

import com.checkmarx.integrations.datastore.api.shared.ApiTestState;
import com.checkmarx.integrations.datastore.api.shared.RepoInitializer;
import com.checkmarx.integrations.datastore.api.shared.UrlFormatter;
import com.checkmarx.integrations.datastore.models.ScmType;
import com.checkmarx.integrations.datastore.repositories.ScmTypeRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

@Slf4j
@RequiredArgsConstructor
@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ScmApiSteps {

    @LocalServerPort
    private int port;

    private final ScmTypeRepository scmTypeRepo;
    private final TestRestTemplate restTemplate;
    private final ModelMapper modelMapper;
    private final ApiTestState testState;
    private final RepoInitializer repoInitializer;
    private final UrlFormatter urlFormatter;

    @Given("database contains the following SCM types:")
    public void databaseContainsScmTypes(List<Map<String, String>> scmTypes) {
        List<ScmType> typesToSave = scmTypes.stream()
                .map(scmType -> modelMapper.map(scmType, ScmType.class))
                .collect(Collectors.toList());
        scmTypeRepo.saveAll(typesToSave);
        scmTypeRepo.flush();
    }

    @Given("database contains the following SCMs:")
    public void databaseContainsScms(List<Map<String, String>> scms) {
        repoInitializer.createScms(scms);
    }

    @When("API client calls the `get SCMs` API")
    public void clientCallsGetScms() {
        URI url = urlFormatter.format("scms", port);
        testState.setLastResponse(restTemplate.getForEntity(url, JsonNode.class));
    }

    @When("API client calls the `get scm` API for SCM ID: {int}")
    public void clientCallsGetScm(int scmId) {
        URI url = urlFormatter.format("scms/{scmId}", port, scmId);
        testState.setLastResponse(restTemplate.getForEntity(url, JsonNode.class));
    }

    @Then("the response is an array of {int} objects with the following fields:")
    public void responseIsArrayWithFields(int expectedItemCount, List<Map<String, String>> expectedResponseItems) {
        JsonNode responseJson = testState.getResponseBodyOrThrow();
        assertEquals("Response is not a JSON array.", JsonNodeType.ARRAY, responseJson.getNodeType());
        assertEquals("Wrong item count in response.", expectedItemCount, responseJson.size());
        validateItemsMatch(expectedResponseItems, responseJson);
    }

    @Then("response contains the following fields:")
    public void responseContainsFields(List<Map<String, String>> expectedFieldValues) {
        JsonNode responseJson = testState.getResponseBodyOrThrow();
        validateFieldsMatch(expectedFieldValues.get(0), responseJson);
    }

    private static void validateItemsMatch(List<Map<String, String>> expectedResponseItems, JsonNode responseJson) {
        for (JsonNode actualItem : responseJson) {
            Map<String, String> expectedItem = findExpectedItem(actualItem.get("id"), expectedResponseItems);
            validateFieldsMatch(expectedItem, actualItem);
        }
    }

    private static Map<String, String> findExpectedItem(JsonNode idNode, List<Map<String, String>> expectedItems) {
        assertNotNull("Response item doesn't have an ID.", idNode);
        String id = idNode.asText();
        Optional<Map<String, String>> foundItem = expectedItems.stream()
                .filter(item -> item.get("id").equals(id))
                .findFirst();
        assertTrue(String.format("Unexpected response item ID: %s", id), foundItem.isPresent());
        return foundItem.get();
    }

    private static void validateFieldsMatch(Map<String, String> expectedItem, JsonNode actualItem) {
        assertEquals("Unexpected property count in response item.", expectedItem.size(), actualItem.size());
        for (Map.Entry<String, String> prop : expectedItem.entrySet()) {
            String expectedValue = prop.getValue();
            String actualValue = actualItem.get(prop.getKey()).asText();
            assertEquals(String.format("Value mismatch for property: %s", prop.getKey()), expectedValue, actualValue);
        }
    }
}

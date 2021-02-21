package com.checkmarx.integrations.datastore.api.scan_details;

import com.checkmarx.integrations.datastore.models.ScanDetails;
import com.checkmarx.integrations.datastore.repositories.ScanDetailsRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.cucumber.java.en.*;
import io.cucumber.spring.CucumberContextConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.Assert.*;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RequiredArgsConstructor
@Slf4j
public class ScanDetailsSteps {
    private static final String MISSING_INDICATOR = "<missing>";
    private final static ObjectMapper objectMapper = new ObjectMapper();

    @LocalServerPort
    private int port;

    private final ScanDetailsRepository repo;

    private final TestRestTemplate restTemplate;

    private ResponseEntity<String> response;

    @Given("database initially contains scan details with scan ID: {word} and body: {string}")
    public void databaseInitiallyContains(String scanId, String body) throws JsonProcessingException {
        repo.save(ScanDetails.builder()
                .scanId(scanId)
                .body(objectMapper.readTree(body))
                .build());
    }

    @When("API client calls the `create scan details` API with scan ID: {word} and body: {string}")
    public void clientCallsTheCreateScanDetailsAPI(String scanId, String body) throws JsonProcessingException {
        ObjectNode requestContents = getCreationRequestContents(scanId, body);
        HttpEntity<ObjectNode> request = new HttpEntity<>(requestContents);
        String url = String.format("http://localhost:%d/scanDetails", port);
        response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
    }

    @When("API client calls the `get scan details` API with scan ID: {word}")
    public void clientCallsTheGetScanDetailsAPI(String scanId) {
        String url = String.format("http://localhost:%d/scanDetails/%s", port, scanId);
        response = restTemplate.getForEntity(url, String.class);
    }

    @Then("response status is {int}")
    public void responseStatusIs(int expectedStatus) {
        assertEquals("Unexpected response status code.", expectedStatus, response.getStatusCodeValue());
    }

    @Then("response content is {string}")
    public void responseContentIs(String expectedContent) {
        assertNotNull("Response is null.", response);
        assertEquals("Unexpected response content.", expectedContent, response.getBody());
    }

    @Then("database contains scan details with scan ID: {word} and body: {string}")
    public void databaseContainsScanDetails(String scanId, String body) {
        Optional<ScanDetails> scanDetails = repo.findByScanId(scanId);
        assertTrue("Expected the DB to contain scan details, but it doesn't.", scanDetails.isPresent());
        String expectedBody = getActualOrEmptyValue(body);
        assertEquals("Unexpected scan details body.", expectedBody, scanDetails.get().getBody().toString());
    }

    @Then("database does not contain scan details for scan ID: {word}")
    public void databaseContainsOrNotScanDetails(String scanId) {
        Optional<ScanDetails> scanDetails = repo.findByScanId(scanId);
        assertFalse("Expected scan details to not exist in the DB, but they do.", scanDetails.isPresent());
    }

    private ObjectNode getCreationRequestContents(String scanId, String body) throws JsonProcessingException {
        ObjectNode requestContents = objectMapper.createObjectNode();
        if (!scanId.equals(MISSING_INDICATOR)) {
            String effectiveValue = getActualOrEmptyValue(scanId);
            requestContents.put("scanId", effectiveValue);
        }
        if (!body.equals(MISSING_INDICATOR)) {
            JsonNode bodyJson = objectMapper.readTree(getActualOrEmptyValue(body));
            requestContents.set("body", bodyJson);
        }
        return requestContents;
    }

    private static String getActualOrEmptyValue(String value) {
        return value.equals("<empty>") ? "" : value;
    }
}

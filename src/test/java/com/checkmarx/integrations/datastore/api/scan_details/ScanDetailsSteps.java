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
    private final static ObjectMapper objectMapper = new ObjectMapper();

    @LocalServerPort
    private int port;

    private final ScanDetailsRepository repo;

    private final TestRestTemplate restTemplate;

    private ResponseEntity<String> response;

    @Given("database initially contains scan details with scan ID: {word} and body: {string}")
    public void databaseInitiallyContains(String scanId, String body) {
        repo.save(ScanDetails.builder()
                .scanId(scanId)
                .body(body)
                .build());
    }

    @When("API client calls the `create scan details` API with scan ID: {word} and body: {string}")
    public void clientCallsTheCreateScanDetailsAPI(String scanId, String body) {
        ObjectNode requestContents = objectMapper.createObjectNode();
        setEffectiveFieldValue("scanId", scanId, requestContents);
        setEffectiveFieldValue("body", body, requestContents);

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

    @Then("response contains {string} field set to {string}")
    public void responseContainsFieldSetTo(String fieldName, String expectedFieldValue) throws JsonProcessingException {
        assertNotNull("Response is null.", response);
        JsonNode responseJson = objectMapper.readTree(response.getBody());
        JsonNode propertyNode = responseJson.get(fieldName);
        assertNotNull("Field value is null.", propertyNode);
        assertEquals("Unexpected field value.", expectedFieldValue, propertyNode.textValue());
    }

    @Then("database {string} scan details with scan ID: {word} and body: {string}")
    public void databaseContainsOrNotScanDetails(String containsOrNot, String scanId, String body) {
        Optional<ScanDetails> scanDetails = repo.findByScanId(scanId);
        log.info("Checking database for scan ID: {}", scanId);
        if (containsOrNot.equals("contains")) {
            assertTrue("Expected the DB to contain scan details, but it doesn't.", scanDetails.isPresent());
            String expectedBody = getActualOrEmptyValue(body);
            assertEquals("Unexpected scan details body.", expectedBody, scanDetails.get().getBody());
        } else {
            assertFalse("Expected scan details to not exist in the DB, but they do.", scanDetails.isPresent());
        }
    }

    private void setEffectiveFieldValue(String fieldName, String fieldValue, ObjectNode target) {
        if (!fieldValue.equals("<missing>")) {
            String effectiveValue = getActualOrEmptyValue(fieldValue);
            target.put(fieldName, effectiveValue);
        }
    }

    private static String getActualOrEmptyValue(String value) {
        return value.equals("<empty>") ? "" : value;
    }
}

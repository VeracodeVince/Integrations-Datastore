package com.checkmarx.integrations.datastore.api.organization_api;

import com.checkmarx.integrations.datastore.api.shared.ApiTestState;
import com.checkmarx.integrations.datastore.api.shared.SharedSteps;
import com.checkmarx.integrations.datastore.api.shared.UrlFormatter;
import com.checkmarx.integrations.datastore.dto.SCMOrgUpdateDto;
import com.fasterxml.jackson.databind.JsonNode;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * Steps that contain calls to the Organizations API.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@CucumberContextConfiguration
@RequiredArgsConstructor
public class OrgApiCallSteps {

    @LocalServerPort
    private int port;

    private final ApiTestState testState;
    private final UrlFormatter urlFormatter;
    private final TestRestTemplate restTemplate;

    @When("API client calls the `get organization` API with scmId: {int} and orgIdentity: {word}")
    public void clientCallsApiWithScmId(int scmId, String orgIdentity) {
        URI url = urlFormatter.format("scms/{scmId}/orgs/{orgIdentity}", port, scmId, orgIdentity);
        ResponseEntity<JsonNode> response = restTemplate.getForEntity(url, JsonNode.class);
        testState.setLastResponse(response);
    }

    @When("API client calls the `get organization` API with repo base URL: {word} and orgIdentity: {word}")
    public void clientCallsApiWithRepoBaseUrl(String repoBaseUrl, String orgIdentity) {
        URI url = urlFormatter.format("orgs?orgIdentity={orgIdentity}&repoBaseUrl={repoBaseUrl}",
                port, orgIdentity, repoBaseUrl);
        ResponseEntity<JsonNode> response = restTemplate.getForEntity(url, JsonNode.class);
        testState.setLastResponse(response);
    }

    @When("API client calls the `store organization` API with scmId: {int}, orgIdentity: {word}, team: {word}, cxFlowConfig: {word}")
    public void clientCallsStoreOrgApi(long scmId, String orgIdentity, String team, String cxFlowConfig) {
        URI url = urlFormatter.format("scms/{scmId}/orgs/{orgIdentity}", port, scmId, orgIdentity);
        SCMOrgUpdateDto request = SCMOrgUpdateDto.builder()
                .team(SharedSteps.getEffectiveValue(team))
                .cxFlowConfig(SharedSteps.getEffectiveValue(cxFlowConfig))
                .build();
        ResponseEntity<JsonNode> response = restTemplate.postForEntity(url, request, JsonNode.class);
        testState.setLastResponse(response);
    }

    @When("API client calls the `import organizations` API request with scmId: {int} and the following items:")
    public void clientCallsImportOrgsApi(long scmId, List<Map<String, String>> requestItems) {
        URI url = urlFormatter.format("scms/{scmId}/orgs", port, scmId);
        HttpEntity<List<Map<String, String>>> request = new HttpEntity<>(requestItems);
        ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.PUT, request, JsonNode.class);
        testState.setLastResponse(response);
    }
}

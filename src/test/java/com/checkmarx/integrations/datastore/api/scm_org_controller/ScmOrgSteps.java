package com.checkmarx.integrations.datastore.api.scm_org_controller;

import com.checkmarx.integrations.datastore.dto.CxFlowPropertiesDto;
import com.checkmarx.integrations.datastore.dto.SCMOrgDto;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@CucumberContextConfiguration
@Slf4j
public class ScmOrgSteps {

    private static final String SCM_URL = "githubTest.com";
    private static final String ORG_IDENTITY = "orgNameTest";

    private ResponseEntity postEndPointResponse;
    private ResponseEntity<CxFlowPropertiesDto> getEndPointResponse;
    private CxFlowPropertiesDto cxFlowPropertiesDto;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @When("storeScmOrg endpoint is getting called")
    public void storeScmOrgEndPointIsGettingCalled() {
        String path = String.format("http://localhost:%s/orgs", port);
        SCMOrgDto scmOrgDto = SCMOrgDto.builder()
                .scmUrl(SCM_URL)
                .orgIdentity(ORG_IDENTITY)
                .build();

        postEndPointResponse = restTemplate.postForEntity(path, scmOrgDto, ResponseEntity.class);
    }

    @Then("{string} response status is {int}")
    public void validateResponseCode(String endPoint, int statusCode) {
        switch (endPoint) {
            case "storeScmOrg":
                statusCode = postEndPointResponse.getStatusCodeValue();
                break;
            case "getCxFlowProperties":
                statusCode = getEndPointResponse.getStatusCodeValue();
                break;
        }
        Assert.assertEquals(endPoint + " response status is not as expected",
                statusCode, statusCode);
    }

    @When("getCxFlowProperties endpoint is getting called")
    public void getCxFlowPropertiesEndPointIsGettingCalled() {
        String path = String.format("http://localhost:%s/orgs/properties", port);
        URI uri = UriComponentsBuilder.fromUriString(path)
                .queryParam("scmBaseUrl", SCM_URL)
                .queryParam("orgIdentity", ORG_IDENTITY)
                .build()
                .toUri();
        getEndPointResponse = restTemplate.getForEntity(uri, CxFlowPropertiesDto.class);
        cxFlowPropertiesDto = getEndPointResponse.getBody();
    }

    @And("response contains scmUrl field set to {string}")
    public void validateScmUrlField(String scmUrl) {
        Assert.assertEquals("Scm URL is not as expected", scmUrl, cxFlowPropertiesDto.getScmUrl());
    }

    @And("response contains orgIdentity field set to {string}")
    public void validateOrgIdentityField(String orgIdentity) {
        Assert.assertEquals("Org identity is not as expected", orgIdentity, cxFlowPropertiesDto.getOrgIdentity());
    }

    @When("getCxFlowProperties endpoint is getting called with invalid scm org")
    public void getCxFlowPropertiesEndPointWithInvalidScmOrg() {
        String path = String.format("http://localhost:%s/orgs/properties", port);
        URI uri = UriComponentsBuilder.fromUriString(path)
                .queryParam("scmBaseUrl", "invalidGithubTest.com")
                .queryParam("orgIdentity", "invalidOrgNameTest")
                .build()
                .toUri();
        getEndPointResponse = restTemplate.getForEntity(uri, CxFlowPropertiesDto.class);
    }
}
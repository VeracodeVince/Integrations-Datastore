package com.checkmarx.integrations.datastore.api.scm_org_controller;

import com.checkmarx.integrations.datastore.dto.SCMOrgDto;
import com.checkmarx.integrations.datastore.dto.SCMDto;
import com.checkmarx.integrations.datastore.dto.SCMOrgLegacyDto;
import com.checkmarx.integrations.datastore.models.ScmOrg;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@CucumberContextConfiguration
@Slf4j
public class ScmOrgSteps {

    @LocalServerPort
    private int port;

    private static final String SCM_URL = "githubTest.com";

    // TODO: Added as a stub for the test to compile. Should be fixed.
    private static final String SCM_URL_TO_FIX = "fixme.org";

    private static final String ORG_IDENTITY = "orgNameTest";
    private static final String CX_FLOW_URL = "Cxflow.com";
    private static final String CX_GO_TOKEN = "cx-go-token-123";
    private static final String CX_TEAM = "cx-team-test";

    private ResponseEntity<SCMOrgDto> getCxFlowPropertiesResponse;
    private SCMOrgDto scmOrg;
    private String orgsPath;
    private String orgsPropertiesPath;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void init() {
        orgsPath = String.format("http://localhost:%s/orgs", port);
        orgsPropertiesPath = String.format("http://localhost:%s/orgs/properties", port);
    }

    @Then("the response status is {int}")
    public void validateResponseCode(int expectedStatusCode) {
        int actualStatusCode =  getCxFlowPropertiesResponse.getStatusCodeValue();
        Assert.assertEquals("Response status code is not as expected",
                expectedStatusCode, actualStatusCode);
    }

    @Given("Scm org with {string} as scm-url and {string} as org identity is stored in database")
    public void initData(String scmUrl, String orgIdentity) {
        List<SCMOrgLegacyDto> orgs = new ArrayList<>();
        SCMOrgLegacyDto org = SCMOrgLegacyDto.builder()
                .scmUrl(scmUrl)
                .orgIdentity(orgIdentity)
                .build();
        orgs.add(org);

        storeScmOrgTokenList(orgs);
    }

    @When("getCxFlowProperties endpoint is getting called with {string} as scm-url and {string} as org identity")
    public void getCxFlowPropertiesEndPointIsGettingCalled(String scmUrl, String orgIdentity) {
        URI uri = getCxFlowPropertiesEndPointUri(scmUrl, orgIdentity);
        getCxFlowPropertiesResponse = restTemplate.getForEntity(uri, SCMOrgDto.class);
        scmOrg = getCxFlowPropertiesResponse.getBody();
    }

    @And("response contains scmUrl field set to {string}")
    public void validateScmUrlField(String scmUrl) {
        Assert.assertEquals("Scm URL is not as expected", scmUrl, SCM_URL_TO_FIX);
    }

    @And("response contains orgIdentity field set to {string}")
    public void validateOrgIdentityField(String orgIdentity) {
        Assert.assertEquals("Org identity is not as expected", orgIdentity, scmOrg.getOrgIdentity());
    }

    @When("getCxFlowProperties endpoint is getting called with invalid scm org parameter")
    public void getCxFlowPropertiesEndPointWithInvalidScmOrg() {
        URI uri = getCxFlowPropertiesEndPointUri("invalidGithubTest.com", "invalidOrgNameTest");
        getCxFlowPropertiesResponse = restTemplate.getForEntity(uri, SCMOrgDto.class);
    }

    @Given("cx-flow details are stored into database")
    public void setCxFlowDetailsInDataBase() {
        createScmInDb();
        SCMOrgDto scmOrg = SCMOrgDto.builder()
                .orgIdentity(ORG_IDENTITY)
                .cxFlowUrl(CX_FLOW_URL)
                .cxGoToken(CX_GO_TOKEN)
                .team(CX_TEAM)
                .build();
        restTemplate.postForEntity(orgsPropertiesPath, scmOrg, ScmOrg.class);
    }

    @And("CxFlowProperties DTO details are fully retrieved")
    public void validateCxFlowDtoDetails() {
        URI uri = getCxFlowPropertiesEndPointUri(SCM_URL, ORG_IDENTITY);
        getCxFlowPropertiesResponse = restTemplate.getForEntity(uri, SCMOrgDto.class);
        SCMOrgDto scmOrg = getCxFlowPropertiesResponse.getBody();

        Assert.assertEquals("Scm URL is not as expected",
                SCM_URL, SCM_URL_TO_FIX);
        Assert.assertEquals("Org identity is not as expected"
                ,ORG_IDENTITY, Objects.requireNonNull(scmOrg).getOrgIdentity());
        Assert.assertEquals("Cx-Flow URL is not as expected"
                ,CX_FLOW_URL, Objects.requireNonNull(scmOrg).getCxFlowUrl());
        Assert.assertEquals("Cx-GO token is not as expected"
                ,CX_GO_TOKEN, Objects.requireNonNull(scmOrg).getCxGoToken());
        Assert.assertEquals("Cx-team token is not as expected"
                ,CX_TEAM, Objects.requireNonNull(scmOrg).getTeam());
    }

    private URI getCxFlowPropertiesEndPointUri(String scmUrl, String orgIdentity) {
        return UriComponentsBuilder.fromUriString(orgsPropertiesPath)
                .queryParam("scmBaseUrl", scmUrl)
                .queryParam("orgIdentity", orgIdentity)
                .build()
                .toUri();
    }

    private void createScmInDb() {
        String path = String.format("http://localhost:%s/scms/storeScm", port);
        SCMDto scmDto = SCMDto.builder()
                .authBaseUrl(SCM_URL)
                .build();
        restTemplate.postForEntity(path, scmDto, ResponseEntity.class);
    }

    private void storeScmOrgTokenList(List<SCMOrgLegacyDto> orgs) {
        createScmInDb();
        restTemplate.put(orgsPath, orgs, ResponseEntity.class);
    }
}
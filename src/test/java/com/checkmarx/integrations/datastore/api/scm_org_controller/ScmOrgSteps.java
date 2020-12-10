package com.checkmarx.integrations.datastore.api.scm_org_controller;

import com.checkmarx.integrations.datastore.dto.CxFlowPropertiesDto;
import com.checkmarx.integrations.datastore.dto.SCMDto;
import com.checkmarx.integrations.datastore.dto.SCMOrgDto;
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
    private static final String ORG_IDENTITY = "orgNameTest";
    private static final String CX_FLOW_URL = "Cxflow.com";
    private static final String CX_GO_TOKEN = "cx-go-token-123";
    private static final String CX_TEAM = "cx-team-test";
    private static final String ACCESS_TOKEN = "token-1234";
    private static final String TOKEN_TYPE = "access-token";

    private ResponseEntity<CxFlowPropertiesDto> getCxFlowPropertiesResponse;
    private ResponseEntity<SCMOrgDto> getScmOrgByNameResponse;
    private CxFlowPropertiesDto cxFlowPropertiesDto;
    private String orgsPath;
    private String orgsPropertiesPath;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void init() {
        orgsPath = String.format("http://localhost:%s/orgs", port);
        orgsPropertiesPath = String.format("http://localhost:%s/orgs/properties", port);
    }

    @When("storeScmOrgToken endpoint is getting called with partial SCMOrg DTO as a list parameters")
    public void storeScmOrgTokenEndPointIsGettingCalled() {
        List<SCMOrgDto> scmOrgDtoList = new ArrayList<>();
        SCMOrgDto scmOrgDto = SCMOrgDto.builder()
                .scmUrl(SCM_URL)
                .orgName(ORG_IDENTITY)
                .build();
        scmOrgDtoList.add(scmOrgDto);

        storeScmOrgTokenList(scmOrgDtoList);
    }

    @And("getScmOrgByName endpoint is getting called with {string} as scm-url and {string} as org name")
    public void getScmOrgByNameEndPointIsGettingCalled(String scmUrl, String scmName) {
        URI uri = getScmOrgByNameEndPointUri(scmUrl, scmName);
        getScmOrgByNameResponse = restTemplate.getForEntity(uri, SCMOrgDto.class);
    }

    @Then("getScmOrgByName response contains scmUrl field set to {string}")
    public void validateGetScmOrgNameScmUrlParameter(String scmUrl) {
        SCMOrgDto scmOrgDto = getScmOrgByNameResponse.getBody();
        Assert.assertEquals("Scm URL value by GET ScmOrgName endpoint is not as expected",
                scmUrl, Objects.requireNonNull(scmOrgDto).getScmUrl());
    }

    @And("response contains orgName field set to {string}")
    public void validateGetScmOrgNameOrgNameParameter(String orgName) {
        SCMOrgDto scmOrgDto = getScmOrgByNameResponse.getBody();
        Assert.assertEquals("Org name value by GET ScmOrgName endpoint is not as expected",
                orgName, Objects.requireNonNull(scmOrgDto).getOrgName());
    }

    @When("storeScmOrgToken endpoint is getting called with full SCMOrg DTO as a list parameters")
    public void storeScmOrgTokenEndPointIsGettingCalledWithToken() {
        List<SCMOrgDto> scmOrgDtoList = new ArrayList<>();
        SCMOrgDto scmOrgDto = SCMOrgDto.builder()
                .scmUrl(SCM_URL)
                .orgName(ORG_IDENTITY)
                .orgIdentity(ORG_IDENTITY)
                .accessToken(ACCESS_TOKEN)
                .tokenType(TOKEN_TYPE)
                .build();
        scmOrgDtoList.add(scmOrgDto);

        storeScmOrgTokenList(scmOrgDtoList);
    }

    @Then("getScmOrgByName response contains a full SCMOrg details")
    public void validateGetScmOrgNameWithToken() {
        SCMOrgDto scmOrgDto = getScmOrgByNameResponse.getBody();
        Assert.assertEquals("Scm URL value by GET ScmOrgName endpoint is not as expected",
                SCM_URL, Objects.requireNonNull(scmOrgDto).getScmUrl());

        Assert.assertEquals("Org name value by GET ScmOrgName endpoint is not as expected",
                ORG_IDENTITY, Objects.requireNonNull(scmOrgDto).getOrgName());

        Assert.assertEquals("Access token value by GET ScmOrgName endpoint is not as expected",
                ACCESS_TOKEN, Objects.requireNonNull(scmOrgDto).getAccessToken());

        Assert.assertEquals("Token type value by GET ScmOrgName endpoint is not as expected",
                TOKEN_TYPE, Objects.requireNonNull(scmOrgDto).getTokenType());
    }

    @Then("{string} response status is {int}")
    public void validateResponseCode(String endPoint, int expectedStatusCode) {
        int actualStatusCode = 0;

        switch (endPoint) {
            case "getCxFlowProperties":
                actualStatusCode = getCxFlowPropertiesResponse.getStatusCodeValue();
                break;
        }
        Assert.assertEquals(endPoint + " response status code is not as expected",
                expectedStatusCode, actualStatusCode);
    }

    @Given("Scm org with {string} as scm-url and {string} as org identity is stored in database")
    public void initData(String scmUrl, String orgIdentity) {
        List<SCMOrgDto> scmOrgDtoList = new ArrayList<>();
        SCMOrgDto scmOrgDto = SCMOrgDto.builder()
                .scmUrl(scmUrl)
                .orgIdentity(orgIdentity)
                .build();
        scmOrgDtoList.add(scmOrgDto);

        storeScmOrgTokenList(scmOrgDtoList);
    }

    @When("getCxFlowProperties endpoint is getting called with {string} as scm-url and {string} as org identity")
    public void getCxFlowPropertiesEndPointIsGettingCalled(String scmUrl, String orgIdentity) {
        URI uri = getCxFlowPropertiesEndPointUri(scmUrl, orgIdentity);
        getCxFlowPropertiesResponse = restTemplate.getForEntity(uri, CxFlowPropertiesDto.class);
        cxFlowPropertiesDto = getCxFlowPropertiesResponse.getBody();
    }

    @And("response contains scmUrl field set to {string}")
    public void validateScmUrlField(String scmUrl) {
        Assert.assertEquals("Scm URL is not as expected", scmUrl, cxFlowPropertiesDto.getScmUrl());
    }

    @And("response contains orgIdentity field set to {string}")
    public void validateOrgIdentityField(String orgIdentity) {
        Assert.assertEquals("Org identity is not as expected", orgIdentity, cxFlowPropertiesDto.getOrgIdentity());
    }

    @When("getCxFlowProperties endpoint is getting called with invalid scm org parameter")
    public void getCxFlowPropertiesEndPointWithInvalidScmOrg() {
        URI uri = getCxFlowPropertiesEndPointUri("invalidGithubTest.com", "invalidOrgNameTest");
        getCxFlowPropertiesResponse = restTemplate.getForEntity(uri, CxFlowPropertiesDto.class);
    }

    @Given("cx-flow details are stored into database")
    public void setCxFlowDetailsInDataBase() {
        createScmInDb();
        CxFlowPropertiesDto cxFlowPropertiesDto = CxFlowPropertiesDto.builder()
                .scmUrl(SCM_URL)
                .orgIdentity(ORG_IDENTITY)
                .cxFlowUrl(CX_FLOW_URL)
                .cxGoToken(CX_GO_TOKEN)
                .cxTeam(CX_TEAM)
                .build();
        restTemplate.postForEntity(orgsPropertiesPath, cxFlowPropertiesDto, ScmOrg.class);
    }

    @And("CxFlowProperties DTO details are fully retrieved")
    public void validateCxFlowDtoDetails() {
        URI uri = getCxFlowPropertiesEndPointUri(SCM_URL, ORG_IDENTITY);
        getCxFlowPropertiesResponse = restTemplate.getForEntity(uri, CxFlowPropertiesDto.class);
        CxFlowPropertiesDto cxFlowPropertiesDto = getCxFlowPropertiesResponse.getBody();

        Assert.assertEquals("Scm URL is not as expected",
                SCM_URL, Objects.requireNonNull(cxFlowPropertiesDto).getScmUrl());
        Assert.assertEquals("Org identity is not as expected"
                ,ORG_IDENTITY, Objects.requireNonNull(cxFlowPropertiesDto).getOrgIdentity());
        Assert.assertEquals("Cx-Flow URL is not as expected"
                ,CX_FLOW_URL, Objects.requireNonNull(cxFlowPropertiesDto).getCxFlowUrl());
        Assert.assertEquals("Cx-GO token is not as expected"
                ,CX_GO_TOKEN, Objects.requireNonNull(cxFlowPropertiesDto).getCxGoToken());
        Assert.assertEquals("Cx-team token is not as expected"
                ,CX_TEAM, Objects.requireNonNull(cxFlowPropertiesDto).getCxTeam());
    }

    private URI getCxFlowPropertiesEndPointUri(String scmUrl, String orgIdentity) {
        return UriComponentsBuilder.fromUriString(orgsPropertiesPath)
                .queryParam("scmBaseUrl", scmUrl)
                .queryParam("orgIdentity", orgIdentity)
                .build()
                .toUri();
    }

    private URI getScmOrgByNameEndPointUri(String scmUrl, String orgName) {
        return UriComponentsBuilder.fromUriString(orgsPath)
                .queryParam("scmBaseUrl", scmUrl)
                .queryParam("orgName", orgName)
                .build()
                .toUri();
    }

    private void createScmInDb() {
        String path = String.format("http://localhost:%s/scms/storeScm", port);
        SCMDto scmDto = SCMDto.builder()
                .baseUrl(SCM_URL)
                .build();
        restTemplate.postForEntity(path, scmDto, ResponseEntity.class);
    }

    private SCMOrgDto getScmOrgDto(String scmUrl, String orgIdentity) {
        return SCMOrgDto.builder()
                .scmUrl(scmUrl)
                .orgIdentity(orgIdentity)
                .build();
    }

    private void storeScmOrgTokenList(List<SCMOrgDto> scmOrgDtoList) {
        createScmInDb();
        restTemplate.put(orgsPath, scmOrgDtoList, ResponseEntity.class);
    }
}
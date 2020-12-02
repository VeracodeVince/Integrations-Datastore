package com.checkmarx.integrations.datastore.api.scm_token_controller;

import com.checkmarx.integrations.datastore.dto.SCMAccessTokenDto;
import com.checkmarx.integrations.datastore.dto.SCMDto;
import com.checkmarx.integrations.datastore.models.Scm;
import com.checkmarx.integrations.datastore.repositories.ScmRepository;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ScmTokenSteps {

    private static final String GITHUB_URL = "github.com";
    private static final String ORG_IDENTITY = "orgIdentityTest";
    private static final String ACCESS_TOKEN = "access-token-ff718111a48803ba";
    private static final String TOKEN_TYPE = "access-token";

    @LocalServerPort
    private int port;

    private SCMAccessTokenDto scmAccessTokenDto;
    private ResponseEntity<SCMAccessTokenDto> storeResponseEntity;
    private ResponseEntity<SCMAccessTokenDto> getResponseEntity;

    @Autowired
    private ScmRepository scmRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    @Given("data base contains {string}, {string} and {string}")
    public void initDataBaseValues(String baseUrl, String clientId, String clientSecret) {
        scmRepository.saveAndFlush(Scm.builder()
                                           .baseUrl(baseUrl)
                                           .clientId(clientId)
                                           .clientSecret(clientSecret)
                                           .build());
    }

    @When("storeScmAccessToken endpoint is getting called with {string}")
    public void storeScmAccessTokenEndpointIsGettingCalledWith(String scmUrl) {
        List<SCMAccessTokenDto> scmAccessTokenDtoList = prepareScmAccessTokenDtos(scmUrl);
        String path = String.format("http://localhost:%s/tokens/storeScmAccessToken", port);
        final HttpEntity<List<SCMAccessTokenDto>> request = new HttpEntity<>(scmAccessTokenDtoList, null);
        storeResponseEntity = restTemplate.exchange(path, HttpMethod.PUT, request,
                                                    SCMAccessTokenDto.class);
    }

    @Then("{string} response status is {int}")
    public void validateResponseCode(String endPoint, int expectedStatusCode) {
        int actualStatusCode = 0;
        switch (endPoint) {
            case "storeScmAccessToken":
                actualStatusCode = storeResponseEntity.getStatusCodeValue();
                break;
            case "getScmAccessToken":
                actualStatusCode = getResponseEntity.getStatusCodeValue();
                break;
        }
        Assert.assertEquals(endPoint + " response status is not as expected",
                            expectedStatusCode, actualStatusCode);
    }

    @When("getScmAccessToken endpoint is getting called")
    public void getScmAccessTokenEndpointIsGettingCalled() {
        String path = String.format("http://localhost:%s/tokens?scmUrl=%s&orgIdentity=%s", port,
                                    GITHUB_URL, ORG_IDENTITY);
        getResponseEntity = restTemplate.getForEntity(path, SCMAccessTokenDto.class);
        scmAccessTokenDto = getResponseEntity.getBody();
    }

    @And("response contains scmUrl field set to {string}")
    public void validateScmUrlField(String scmUrl) {
        Assert.assertEquals("Scm URL is not as expected", scmUrl, scmAccessTokenDto.getScmUrl());
    }

    @And("response contains orgIdentity field set to {string}")
    public void validateOrgIdentityField(String orgIdentity) {
        Assert.assertEquals("Org identity is not as expected", orgIdentity, scmAccessTokenDto.getOrgIdentity());
    }

    @And("response contains accessToken field set to {string}")
    public void responseContainsAccessTokenFieldSetTo(String accessToken) {
        Assert.assertEquals("Access token is not as expected", accessToken, scmAccessTokenDto.getAccessToken());
    }

    @And("response contains tokenType field set to {string}")
    public void responseContainsTokenTypeFieldSetTo(String tokenType) {
        Assert.assertEquals("Token type is not as expected", tokenType, scmAccessTokenDto.getTokenType());
    }

    @When("getScmAccessToken endpoint is getting called with invalid scm {string}")
    public void getScmAccessTokenEndpointIsGettingCalledWithInvalidScm(String invalidScmUrl) {
        String path = String.format("http://localhost:%s/tokens?scmUrl=%s&orgIdentity=%s", port,
                                    invalidScmUrl, ORG_IDENTITY);
        getResponseEntity = restTemplate.getForEntity(path, SCMAccessTokenDto.class);
    }

    @When("storeScmAccessToken endpoint is getting called with invalid scm {string}")
    public void storeScmAccessTokenEndpointIsGettingCalledWithInvalidScm(String invalidScmUrl) {
        List<SCMAccessTokenDto> scmAccessTokenDtoList = prepareScmAccessTokenDtos(invalidScmUrl);
        String path = String.format("http://localhost:%s/tokens/storeScmAccessToken", port);
        final HttpEntity<List<SCMAccessTokenDto>> request = new HttpEntity<>(scmAccessTokenDtoList, null);
        storeResponseEntity = restTemplate.exchange(path, HttpMethod.PUT, request,
                                                    SCMAccessTokenDto.class);
    }

    private List<SCMAccessTokenDto> prepareScmAccessTokenDtos(String scmUrl) {
        List<SCMAccessTokenDto> scmAccessTokenDtos = new ArrayList<>();
        scmAccessTokenDtos.add(SCMAccessTokenDto.builder()
                                       .orgIdentity(ORG_IDENTITY)
                                       .scmUrl(scmUrl)
                                       .accessToken(ACCESS_TOKEN)
                                       .tokenType(TOKEN_TYPE)
                                       .build());
        return scmAccessTokenDtos;
    }
}

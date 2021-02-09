package com.checkmarx.integrations.datastore.api.scm_token_controller;

import com.checkmarx.integrations.datastore.dto.AccessTokenShortDto;
import com.checkmarx.integrations.datastore.dto.SCMAccessTokenLegacyDto;
import com.checkmarx.integrations.datastore.models.Scm;
import com.checkmarx.integrations.datastore.repositories.ScmRepository;
import com.checkmarx.integrations.datastore.services.OrgService;
import com.checkmarx.integrations.datastore.services.ScmService;
import com.checkmarx.integrations.datastore.services.TokenService;
import io.cucumber.java.en.*;
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

    private SCMAccessTokenLegacyDto scmAccessTokenDto;
    private ResponseEntity<SCMAccessTokenLegacyDto> storeResponseEntity;
    private ResponseEntity<SCMAccessTokenLegacyDto> getResponseEntity;

    @Autowired
    private ScmService scmService;

    @Autowired
    private OrgService orgService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ScmRepository scmRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    @Given("data base contains {string}, {string} and {string}")
    public void initDataBaseValues(String baseUrl, String clientId, String clientSecret) {
        scmRepository.saveAndFlush(Scm.builder()
                                           .authBaseUrl(baseUrl)
                                           .clientId(clientId)
                                           .clientSecret(clientSecret)
                                           .build());
    }

    @When("storeScmAccessToken endpoint is getting called with {string}")
    public void storeScmAccessTokenEndpointIsGettingCalledWith(String scmUrl) {
        List<SCMAccessTokenLegacyDto> scmAccessTokenDtoList = prepareScmAccessTokenDtos(scmUrl);
        String path = String.format("http://localhost:%s/tokens/storeScmAccessToken", port);
        final HttpEntity<List<SCMAccessTokenLegacyDto>> request = new HttpEntity<>(scmAccessTokenDtoList, null);
        storeResponseEntity = restTemplate.exchange(path, HttpMethod.PUT, request,
                                                    SCMAccessTokenLegacyDto.class);
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
        getResponseEntity = restTemplate.getForEntity(path, SCMAccessTokenLegacyDto.class);
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
        getResponseEntity = restTemplate.getForEntity(path, SCMAccessTokenLegacyDto.class);
    }

    @When("storeScmAccessToken endpoint is getting called with invalid scm {string}")
    public void storeScmAccessTokenEndpointIsGettingCalledWithInvalidScm(String invalidScmUrl) {
        List<SCMAccessTokenLegacyDto> scmAccessTokenDtoList = prepareScmAccessTokenDtos(invalidScmUrl);
        String path = String.format("http://localhost:%s/tokens/storeScmAccessToken", port);
        final HttpEntity<List<SCMAccessTokenLegacyDto>> request = new HttpEntity<>(scmAccessTokenDtoList, null);
        storeResponseEntity = restTemplate.exchange(path, HttpMethod.PUT, request,
                                                    SCMAccessTokenLegacyDto.class);
    }

    private List<SCMAccessTokenLegacyDto> prepareScmAccessTokenDtos(String scmUrl) {
        List<SCMAccessTokenLegacyDto> scmAccessTokenDtos = new ArrayList<>();
        scmAccessTokenDtos.add(SCMAccessTokenLegacyDto.builder()
                                       .orgIdentity(ORG_IDENTITY)
                                       .scmUrl(scmUrl)
                                       .accessToken(ACCESS_TOKEN)
                                       .tokenType(TOKEN_TYPE)
                                       .build());
        return scmAccessTokenDtos;
    }

    @Given("data base contains scm {string} with {string} and {string}")
    public void dataBaseContainsScmWithAndAnd(String scmUrl, String orgIdentity, String accessToken) {
        prepareDataBase(scmUrl, orgIdentity, accessToken);
    }

    private void prepareDataBase(String scmUrl, String orgIdentity, String accessToken) {
        scmRepository.saveAndFlush(Scm.builder()
                                           .authBaseUrl(scmUrl)
                                           .build());
// TODO: fix.
//        Scm scm = scmService.getScmById(scmUrl);
//        orgService.createOrgIfDoesntExist(scm, orgIdentity);
        tokenService.createTokenInfoIfDoesntExist(AccessTokenShortDto.builder()
                .accessToken(accessToken)
                .build());
    }
}

package com.checkmarx.integrations.datastore.api.scm_token_api;

import com.checkmarx.integrations.datastore.api.shared.ApiTestState;
import com.checkmarx.integrations.datastore.api.shared.RepoInitializer;
import com.checkmarx.integrations.datastore.api.shared.UrlFormatter;
import com.checkmarx.integrations.datastore.dto.AccessTokenShortDto;
import com.checkmarx.integrations.datastore.models.Scm;
import com.checkmarx.integrations.datastore.models.ScmOrg;
import com.checkmarx.integrations.datastore.models.Token;
import com.checkmarx.integrations.datastore.repositories.ScmOrgRepository;
import com.checkmarx.integrations.datastore.repositories.ScmRepository;
import com.checkmarx.integrations.datastore.repositories.ScmTokenRepository;
import com.fasterxml.jackson.databind.JsonNode;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Slf4j
@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RequiredArgsConstructor
public class ScmTokenSteps {

    @LocalServerPort
    private int port;

    private final ApiTestState testState;
    private final ScmRepository scmRepo;
    private final ScmOrgRepository orgRepo;
    private final ScmTokenRepository tokenRepo;
    private final UrlFormatter urlFormatter;
    private final TestRestTemplate restTemplate;

    @Given("database contains {string} organization with scmId: {int} and token: {string}")
    public void dbContainsOrg(String orgIdentity, long scmId, String token) {
        createOrgWithToken(orgIdentity, token, scmRepo.getOne(scmId));
    }

    @Given("database contains an access token with ID: {int} and {string} value")
    public void givenDbContainsToken(long id, String token) {
        Token createdToken = createToken(token);
        assertEquals("Wrong token ID after creation.", id, createdToken.getId().longValue());
    }

    @Given("database does not contain access tokens")
    public void dbDoesNotContainTokens() {
        tokenRepo.deleteAll();
    }

    @Given("{word} is the only organization belonging to the SCM with ID {int}")
    public void isTheOnlyOrg(String orgIdentity, long scmId) {
        Scm scmWithId = Scm.builder().id(scmId).build();
        Example<ScmOrg> orgsWithScmId = Example.of(ScmOrg.builder()
                .scm(scmWithId)
                .build());
        long existingOrgCount = orgRepo.count(orgsWithScmId);
        assertEquals("Expected the SCM to have no organizations.", 0, existingOrgCount);

        String tokenValue = String.format("%s-token", orgIdentity);
        createOrgWithToken(orgIdentity, tokenValue, scmWithId);
    }

    @Given("database does not contain an access token with ID: {int}")
    public void dbDoesntContainToken(long id) {
        tokenRepo.findById(id)
                .ifPresent(tokenRepo::delete);
    }

    @When("API client calls the `get access token` API for the {string} organization and scmId: {int}")
    public void clientCallsGetTokenApi(String orgIdentity, long scmId) {
        URI url = urlFormatter.format("tokens?scmId={scmId}&orgIdentity={orgIdentity}",
                port, scmId, orgIdentity);
        ResponseEntity<JsonNode> response = restTemplate.getForEntity(url, JsonNode.class);
        testState.setLastResponse(response);
    }

    @When("API client calls the `update access token` API with token ID: {int} and token value: {string}")
    public void clientCallsUpdateTokenApi(long tokenId, String tokenValue) {
        URI url = urlFormatter.format("tokens/{id}", port, tokenId);
        HttpEntity<AccessTokenShortDto> request = getTokenRequest(tokenValue);
        ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.PUT, request, JsonNode.class);
        testState.setLastResponse(response);
    }

    @When("API client calls the `create access token` API with token value: {string}")
    public void clientCallsCreateTokenApi(String tokenValue) {
        URI url = urlFormatter.format("tokens", port);
        HttpEntity<AccessTokenShortDto> request = getTokenRequest(tokenValue);
        ResponseEntity<JsonNode> response = restTemplate.postForEntity(url, request, JsonNode.class);
        testState.setLastResponse(response);
    }

    @Then("response contains `accessToken` field set to {string} and `id` field set to {int}")
    public void responseContainsFields(String token, long id) {
        JsonNode response = testState.getResponseBodyOrThrow();
        verifyField("accessToken", response, token);
        verifyField("id", response, Long.toString(id));
    }

    @Then("response body is set to {word}")
    public void responseBodyIsSetTo(String body) {
        JsonNode response = testState.getResponseBodyOrThrow();
        assertEquals("Unexpected response body.", body, response.asText());
    }

    @Then("database now/still contains an access token with ID: {int} and {string} value")
    public void thenDbContainsToken(long id, String value) {
        Optional<Token> tokenInfo = tokenRepo.findById(id);
        assertTrue(String.format("Unable to find token by ID: %d", id), tokenInfo.isPresent());
        assertEquals("Unexpected token value.", value, tokenInfo.get().getAccessToken());
    }

    private void createOrgWithToken(String orgIdentity, String tokenValue, Scm one) {
        Token createdToken = createToken(tokenValue);

        ScmOrg orgToCreate = ScmOrg.builder()
                .orgIdentity(orgIdentity)
                .scm(one)
                .accessToken(createdToken)
                .build();
        orgRepo.saveAndFlush(orgToCreate);
    }

    @Then("database does not contain other access tokens with the {string} value")
    public void dbDoesNotContainToken(String value) {
        long tokenCount = tokenRepo.count(Example.of(Token.builder().accessToken(value).build()));
        assertEquals(String.format("Database contains other tokens with the value: '%s'", value), 1, tokenCount);
    }

    private HttpEntity<AccessTokenShortDto> getTokenRequest(String tokenValue) {
        return new HttpEntity<>(AccessTokenShortDto.builder()
                .accessToken(tokenValue)
                .build());
    }

    private Token createToken(String token) {
        return tokenRepo.saveAndFlush(Token.builder()
                .accessToken(token)
                .build());
    }

    private void verifyField(String fieldName, JsonNode json, String expectedValue) {
        assertTrue(String.format("Response doesn't have the '%s' field.", fieldName),
                json.has(fieldName));

        assertEquals(String.format("Unexpected value for the '%s' field.", fieldName),
                expectedValue, json.get(fieldName).asText());
    }
}

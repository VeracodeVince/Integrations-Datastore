package com.checkmarx.integrations.datastore.api.organization_api;

import com.checkmarx.integrations.datastore.api.shared.ApiTestState;
import com.checkmarx.integrations.datastore.api.shared.RepoInitializer;
import com.checkmarx.integrations.datastore.api.shared.SharedSteps;
import com.checkmarx.integrations.datastore.models.ScmOrg;
import com.checkmarx.integrations.datastore.models.Token;
import com.checkmarx.integrations.datastore.repositories.ScmOrgRepository;
import com.checkmarx.integrations.datastore.repositories.ScmRepository;
import com.checkmarx.integrations.datastore.repositories.ScmTokenRepository;
import com.fasterxml.jackson.databind.JsonNode;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Implementation for steps that read from or write to the DB.
 */
@Slf4j
@RequiredArgsConstructor
@Transactional      // Prevent the "LazyInitializationException - no Session" error.
public class OrgDbSteps {
    private final ApiTestState testState;
    private final ScmRepository scmRepo;
    private final ScmOrgRepository orgRepo;
    private final ScmTokenRepository tokenRepo;
    private final RepoInitializer repoInitializer;
    private final ModelMapper modelMapper;

    @Given("database contains the following SCMs:")
    public void databaseContainsScms(List<Map<String, String>> scms) {
        repoInitializer.createScms(scms);
    }

    @Given("database contains SCM tokens with IDs:")
    public void databaseContainsTokens(List<Long> tokenIds) {
        List<Token> tokens = tokenIds.stream()
                .map(toDummyToken())
                .collect(Collectors.toList());
        tokenRepo.saveAll(tokens);
        tokenRepo.flush();
    }

    @Given("database contains the following organizations:")
    public void databaseContainsOrgs(List<Map<String, String>> orgs) {
        List<ScmOrg> orgsToSave = orgs.stream()
                .map(toScmOrg())
                .collect(Collectors.toList());

        orgRepo.saveAll(orgsToSave);
        orgRepo.flush();
    }

    @Then("response fields correspond to an organization with the ID: {int} in the database")
    public void fieldsCorrespondToOrg(long orgId) {
        ScmOrg orgInDB = orgRepo.getOne(orgId);
        assertNotNull(String.format("Org with ID: %d was not found in DB.", orgId), orgInDB);

        JsonNode orgFromResponse = testState.getResponseBodyOrThrow();
        validateJsonField(orgInDB.getId().toString(), orgFromResponse, "id");
        validateJsonField(orgInDB.getOrgIdentity(), orgFromResponse, "orgIdentity");
        validateJsonField(orgInDB.getCxFlowUrl(), orgFromResponse, "cxFlowUrl");
        validateJsonField(orgInDB.getCxFlowConfig(), orgFromResponse, "cxFlowConfig");
        validateJsonField(orgInDB.getTeam(), orgFromResponse, "team");

        validateJsonField(Long.toString(orgInDB.getScm().getId()), orgFromResponse, "scmId");
        validateJsonField(orgInDB.getAccessToken().getId().toString(), orgFromResponse, "tokenId");
    }

    @Then("database contains an organization with scmId: {int}, orgIdentity: {word}, team: {word}, " +
            "cxFlowUrl: {word}, cxFlowConfig: {word}, tokenId: {word}")
    public void thenDatabaseContainsOrg(
            long scmId, String orgIdentity, String team, String cxFlowUrl, String cxFlowConfig, String tokenId) {
        ScmOrg org = orgRepo.getScmOrg(scmId, orgIdentity);

        String message = String.format("Expected the DB to contain an organization with scmID: %d, orgIdentity: '%s'" +
                " - but it doesn't!", scmId, orgIdentity);
        assertNotNull(message, org);

        SharedSteps.validateFieldValue("team", team, org.getTeam());
        SharedSteps.validateFieldValue("cxFlowUrl", cxFlowUrl, org.getCxFlowUrl());
        SharedSteps.validateFieldValue("cxFlowConfig", cxFlowConfig, org.getCxFlowConfig());

        Token token = org.getAccessToken();
        if (tokenId.equals("<null>")) {
            assertNull("Expected access token to be null.", token);
        } else {
            assertNotNull("Access token is null.", token);
            assertNotNull("Access token ID is null.", token.getId());
            SharedSteps.validateFieldValue("tokenId", tokenId, token.getId().toString());
        }
    }

    @Then("database contains, among others, organizations with the following fields:")
    public void thenDatabaseContainsOrgs(List<Map<String, String>> expectedOrgs) {
        for (Map<String, String> expectedOrg : expectedOrgs) {
            thenDatabaseContainsOrg(
                    Long.parseLong(expectedOrg.get("scmId")),
                    expectedOrg.get("orgIdentity"),
                    expectedOrg.get("team"),
                    expectedOrg.get("cxFlowUrl"),
                    expectedOrg.get("cxFlowConfig"),
                    expectedOrg.get("tokenId")
            );
        }
    }

    private Function<Long, Token> toDummyToken() {
        return id -> Token.builder()
                .accessToken(String.format("dummyToken%d", id))
                .build();
    }

    private Function<Map<String, String>, ScmOrg> toScmOrg() {
        return org -> {
            ScmOrg result = modelMapper.map(org, ScmOrg.class);
            setInnerObjects(org, result);
            return result;
        };
    }

    private void setInnerObjects(Map<String, String> org, ScmOrg target) {
        long scmId = Long.parseLong(org.get("scmId"));
        target.setScm(scmRepo.findById(scmId)
                .orElse(null));

        long tokenId = Long.parseLong(org.get("tokenId"));
        target.setAccessToken(tokenRepo.findById(tokenId)
                .orElse(null));
    }

    private static void validateJsonField(String expectedValue, JsonNode actualOrg, String fieldName) {
        String actualValue = actualOrg.get(fieldName).asText();
        assertEquals(String.format("Unexpected org field value. Field name: %s", fieldName), expectedValue, actualValue);
    }
}
package com.checkmarx.integrations.datastore.api.repo_api;

import com.checkmarx.integrations.datastore.api.shared.ApiTestState;
import com.checkmarx.integrations.datastore.api.shared.SharedSteps;
import com.checkmarx.integrations.datastore.api.shared.UrlFormatter;
import com.checkmarx.integrations.datastore.dto.RepoDto;
import com.checkmarx.integrations.datastore.dto.RepoUpdateDto;
import com.checkmarx.integrations.datastore.dto.ReposUpdateDto;
import com.fasterxml.jackson.databind.JsonNode;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@CucumberContextConfiguration
@RequiredArgsConstructor
public class RepoApiCallSteps {
    @LocalServerPort
    private int port;

    private final ApiTestState testState;
    private final UrlFormatter urlFormatter;
    private final TestRestTemplate restTemplate;

    @When("API client calls the `get organization repos` API with scmId: {int} and orgIdentity: {word}")
    public void getRepos(long scmId, String orgIdentity) {
        URI url = urlFormatter.format("scms/{scmId}/orgs/{orgIdentity}/repos", port, scmId, orgIdentity);
        ResponseEntity<JsonNode> response = restTemplate.getForEntity(url, JsonNode.class);
        testState.setLastResponse(response);
    }

    @When("API client calls the `get repo` API with scmId: {int}, orgIdentity: {word}, repoIdentity: {word}")
    public void getRepo(long scmId, String orgIdentity, String repoIdentity) {
        URI url = urlFormatter.format("scms/{scmId}/orgs/{orgIdentity}/repos/{repoIdentity}",
                port, scmId, orgIdentity, repoIdentity);
        ResponseEntity<JsonNode> response = restTemplate.getForEntity(url, JsonNode.class);
        testState.setLastResponse(response);
    }

    @When("API client calls the `import repos` API for scmId: {int}, orgIdentity: {word}, with the following repos:")
    public void importRepos(long scmId, String orgIdentity, List<Map<String, String>> repos) {
        ReposUpdateDto body = getUpdateRequestBody(scmId, orgIdentity, repos);
        HttpEntity<ReposUpdateDto> request = new HttpEntity<>(body);
        URI url = urlFormatter.format("/repos", port);
        ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.PUT, request, JsonNode.class);
        testState.setLastResponse(response);
    }

    @When("API client calls the `import repos` API for scmId: {int}, orgIdentity: {word}")
    public void importRepos(long scmId, String orgIdentity){
        importRepos(scmId, orgIdentity, Collections.emptyList());
    }

    @When("API client calls the `update repo` API with scmId: {int}, orgIdentity: {word}, repoIdentity: {word}, webhook_id: {word}, is_webhook_configured: {}")
    public void updateRepo(long scmId, String orgIdentity, String repoIdentity, String webhookId, boolean isWebhookConfigured) {
        RepoUpdateDto body = RepoUpdateDto.builder()
                .webhookId(SharedSteps.getEffectiveValue(webhookId))
                .isWebhookConfigured(isWebhookConfigured)
                .build();
        HttpEntity<RepoUpdateDto> request = new HttpEntity<>(body);
        URI url = urlFormatter.format("scms/{scmId}/orgs/{orgIdentity}/repos/{repoIdentity}",
                port, scmId, orgIdentity, repoIdentity);
        ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.PUT, request, JsonNode.class);
        testState.setLastResponse(response);
    }

    @When("API client calls the `update repo` API with scmId: {int}, orgIdentity: {word}, repoIdentity: {word}")
    public void updateRepo(long scmId, String orgIdentity, String repoIdentity) {
        updateRepo(scmId, orgIdentity, repoIdentity, "anyWebhookId", true);
    }

    private ReposUpdateDto getUpdateRequestBody(long scmId, String orgIdentity, List<Map<String, String>> repos) {
        return ReposUpdateDto.builder()
                .scmId(scmId)
                .orgIdentity(orgIdentity)
                .repoList(getRepoList(repos))
                .build();
    }

    private List<RepoDto> getRepoList(List<Map<String, String>> repos) {
        return repos.stream()
                .map(this::toRepo)
                .collect(Collectors.toList());
    }

    private RepoDto toRepo(Map<String, String> repoMap) {
        return RepoDto.builder()
                .repoIdentity(repoMap.get("repo_identity"))
                .webhookId(SharedSteps.getEffectiveValue(repoMap.get("webhook_id")))
                .isWebhookConfigured(Boolean.parseBoolean(repoMap.get("is_webhook_configured")))
                .build();
    }
}

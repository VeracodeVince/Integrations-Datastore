package com.checkmarx.integrations.datastore.api.scm_repo_controller;

import com.checkmarx.integrations.datastore.dto.RepoDto;
import com.checkmarx.integrations.datastore.dto.SCMDto;
import com.checkmarx.integrations.datastore.dto.SCMOrgDto;
import com.checkmarx.integrations.datastore.dto.SCMRepoDto;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
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

@CucumberContextConfiguration
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ScmRepoSteps {

    private static final String SCM_URL = "githubTest.com";
    private static final String ORG_IDENTITY = "orgNameTest";
    private static final String INVALID_ORG_IDENTITY = "invalidOrgNameTest";
    private static final String REPO_IDENTITY_1 = "repo-1";
    private static final String REPO_IDENTITY_2 = "repo-2";

    private int REPO_IDENTITY_1_WEBHOOK_ID = RandomUtils.nextInt(1, 1000);
    private int REPO_IDENTITY_2_WEBHOOK_ID = RandomUtils.nextInt(1, 1000);

    private String basicPath;
    private ResponseEntity<RepoDto[]> listResponseEntity;
    private ResponseEntity<RepoDto> responseEntity;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void init() {
        basicPath = String.format("http://localhost:%s/repos", port);

        createScmInDb();
        createScmOrgInDb();

        List<RepoDto> repoDtoList = new ArrayList<>();
        repoDtoList.add(createRepoDto(REPO_IDENTITY_1, REPO_IDENTITY_1_WEBHOOK_ID, true));
        repoDtoList.add(createRepoDto(REPO_IDENTITY_2, REPO_IDENTITY_2_WEBHOOK_ID, false));

        SCMRepoDto scmRepoDto = createScmRepoDto(repoDtoList);
        restTemplate.put(basicPath, scmRepoDto, ResponseEntity.class);
    }

    @When("getCxFlowProperties endpoint is getting called with {string} and {string}")
    public void scmReposByOrgIdentityIsGettingCalled(String scmBaseUrl, String orgIdentity) {
        URI uri = getScmReposByOrgIdentityEndPointUri(basicPath, scmBaseUrl, orgIdentity);
        listResponseEntity = restTemplate.getForEntity(uri, RepoDto[].class);
    }

    @Then("{string} response status is {int}")
    public void validateResponseCode(String endPoint, int expectedStatusCode) {
        int actualStatusCode = 0;

        switch (endPoint) {
            case "getScmReposByOrgIdentity":
                actualStatusCode = listResponseEntity.getStatusCodeValue();
                break;
            case "getScmRepo":
                actualStatusCode = responseEntity.getStatusCodeValue();
                break;
        }
        Assert.assertEquals("Response status code is not as expected",
                expectedStatusCode, actualStatusCode);
    }

    @And("{int} repos are expected to be retrieved back")
    public void validateNumberOfRetrievedRepos(int numberOfRepos) {
        Assert.assertEquals("", numberOfRepos, Objects.requireNonNull(listResponseEntity.getBody()).length);
    }

    @And("a list of Repo DTOs details are fully retrieved")
    public void validateRepoRepoDetails() {
        RepoDto[] repoDtos = listResponseEntity.getBody();

        Assert.assertEquals(REPO_IDENTITY_1, Objects.requireNonNull(repoDtos)[0].getRepoIdentity());
        Assert.assertEquals(REPO_IDENTITY_1_WEBHOOK_ID, Integer.parseInt(repoDtos[0].getWebhookId()));
        Assert.assertTrue(repoDtos[0].isWebhookConfigured());

        Assert.assertEquals(REPO_IDENTITY_2, repoDtos[1].getRepoIdentity());
        Assert.assertEquals(REPO_IDENTITY_2_WEBHOOK_ID, Integer.parseInt(repoDtos[1].getWebhookId()));
        Assert.assertFalse(repoDtos[1].isWebhookConfigured());

    }

    @When("getScmRepo endpoint is getting called with {string} {string} and {string}")
    public void getScmRepoIsGettingCalled(String scmBaseUrl, String orgIdentity, String repoIdentity) {
        String repoPath = basicPath.concat("/").concat(repoIdentity);
        URI uri = getScmReposByOrgIdentityEndPointUri(repoPath, scmBaseUrl, orgIdentity);
        responseEntity = restTemplate.getForEntity(uri, RepoDto.class);
    }

    @And("a Repo DTO details are fully retrieved")
    public void validateRepoDto() {
        RepoDto repoDto = responseEntity.getBody();

        Assert.assertEquals(REPO_IDENTITY_1, Objects.requireNonNull(repoDto).getRepoIdentity());
        Assert.assertEquals(REPO_IDENTITY_1_WEBHOOK_ID, Integer.parseInt(repoDto.getWebhookId()));
        Assert.assertTrue(repoDto.isWebhookConfigured());
    }


    private SCMRepoDto createScmRepoDto(List<RepoDto> repoDtoList) {
        return SCMRepoDto.builder()
                .scmUrl(SCM_URL)
                .orgIdentity(ORG_IDENTITY)
                .repoList(repoDtoList)
                .build();
    }

    private RepoDto createRepoDto(String repoIdentity, int webhookId, boolean isWebhookConfigured) {
        return RepoDto.builder()
                .repoIdentity(repoIdentity)
                .isWebhookConfigured(isWebhookConfigured)
                .webhookId(String.valueOf(webhookId))
                .build();
    }

    private void createScmOrgInDb() {
        String path = String.format("http://localhost:%s/orgs", port);
        SCMOrgDto scmOrgDto = SCMOrgDto.builder()
                .scmUrl(SCM_URL)
                .orgIdentity(INVALID_ORG_IDENTITY)
                .build();

        restTemplate.postForEntity(path, scmOrgDto, ResponseEntity.class);
    }

    private void createScmInDb() {
        String path = String.format("http://localhost:%s/scms/storeScm", port);
        SCMDto scmDto = SCMDto.builder()
                .baseUrl(SCM_URL)
                .build();
        restTemplate.postForEntity(path, scmDto, ResponseEntity.class);
    }

    private URI getScmReposByOrgIdentityEndPointUri(String path, String scmUrl, String orgIdentity) {
        return UriComponentsBuilder.fromUriString(path)
                .queryParam("scmBaseUrl", scmUrl)
                .queryParam("orgIdentity", orgIdentity)
                .build()
                .toUri();
    }
}
package com.checkmarx.integrations.datastore.config;

import com.checkmarx.integrations.datastore.dto.AccessTokenShortDto;
import com.checkmarx.integrations.datastore.dto.SCMOrgShortDto;
import com.checkmarx.integrations.datastore.models.Scm;
import com.checkmarx.integrations.datastore.repositories.ScmRepository;
import com.checkmarx.integrations.datastore.services.OrgService;
import com.checkmarx.integrations.datastore.services.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitConfig {

    private static final String GITHUB_REPO_BASE = "https://github.com";
    private static final String GITLAB_REPO_BASE = "https://gitlab.com";
    private static final String AZURE_REPO_BASE = "https://dev.azure.com";
    private static final String BITBUCKET_REPO_BASE = "https://api.bitbucket.org";

    private static final String GITHUB_DEFAULT_ORG = "GITHUB_DEFAULT_ORG";
    private static final String GITHUB_DEFAULT_TOKEN = "GITHUB_DEFAULT_TOKEN";
    private static final String ACCESS_TOKEN = "access_token";
    private static final String REFRESH_TOKEN = "refresh_token";

    @Value("${github.client.sec}")
    private String githubClientSec;
    @Value("${gitlab.client.sec}")
    private String gitlabClientSec;
    @Value("${azure.client.sec}")
    private String azureClientSec;
    @Value("${bitbucket.client.sec}")
    private String bitbucketClientSec;

    @Value("${github.client.id}")
    private String githubClientId;
    @Value("${gitlab.client.id}")
    private String gitlabClientId;
    @Value("${azure.client.id}")
    private String azureClientId;
    @Value("${bitbucket.client.id}")
    private String bitbucketClientId;

    private final Environment environment;
    private final ScmRepository scmRepository;
    private final TokenService tokenService;
    private final OrgService orgService;

    @PostConstruct
    public void dataInit(){
        log.info("Running post construct data initiation");

        scmRepository.updateScmClientDetails(githubClientSec, githubClientId, GITHUB_REPO_BASE);
        scmRepository.updateScmClientDetails(gitlabClientSec, gitlabClientId, GITLAB_REPO_BASE);
        scmRepository.updateScmClientDetails(azureClientSec, azureClientId, AZURE_REPO_BASE);
        scmRepository.updateScmClientDetails(bitbucketClientSec, bitbucketClientId, BITBUCKET_REPO_BASE);
        if(isDevMode()) {
            insertDefaultData();
        }

    }

    private void insertDefaultData() {
            insertGithubDefault();
    }

    private void insertGithubDefault() {
        String orgIdentity = environment.getProperty(GITHUB_DEFAULT_ORG);
        String orgToken =  environment.getProperty(GITHUB_DEFAULT_TOKEN);
        Scm scm = scmRepository.getScmByRepoBaseUrl(GITHUB_REPO_BASE);

        String tokenJson = getTokenJson(orgToken);
        long tokenId = createDefaultToken(tokenJson);
        createDefaultOrg(orgIdentity, scm, tokenId);
    }

    private void createDefaultOrg(String orgIdentity, Scm scm, long tokenId) {
        orgService.createOrg(SCMOrgShortDto.builder().orgIdentity(orgIdentity).tokenId(tokenId).build(), scm
                .getId());
    }

    private long createDefaultToken(String tokenJson) {
        return tokenService.createTokenInfoIfDoesntExist(
                AccessTokenShortDto.builder().accessToken(tokenJson).build());
    }

    private String getTokenJson(String orgToken) {
        return new JSONObject()
                .put(ACCESS_TOKEN, orgToken)
                .put(REFRESH_TOKEN, "")
                .toString();
    }

    private boolean isDevMode() {
        return !StringUtils.isAnyEmpty(environment.getProperty(GITHUB_DEFAULT_ORG),
                                      environment.getProperty(GITHUB_DEFAULT_TOKEN));
    }
}

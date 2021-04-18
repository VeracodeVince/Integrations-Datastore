package com.checkmarx.integrations.datastore.config;

import com.checkmarx.integrations.datastore.repositories.ScmRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitConfig {

    private static final String GITHUB_REPO_BASE = "https://github.com";
    private static final String GITLAB_REPO_BASE = "https://gitlab.com";
    private static final String AZURE_REPO_BASE = "https://dev.azure.com";
    private static final String BITBUCKET_REPO_BASE = "https://api.bitbucket.org";

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

    private final ScmRepository scmRepository;



    @PostConstruct
    public void dataInit(){
        log.info("Running post construct data initiation");

        scmRepository.updateScmClientDetails(githubClientSec, githubClientId, GITHUB_REPO_BASE);
        scmRepository.updateScmClientDetails(gitlabClientSec, gitlabClientId, GITLAB_REPO_BASE);
        scmRepository.updateScmClientDetails(azureClientSec, azureClientId, AZURE_REPO_BASE);
        scmRepository.updateScmClientDetails(bitbucketClientSec, bitbucketClientId, BITBUCKET_REPO_BASE);
    }
}

package com.checkmarx.integrations.datastore.services;

import com.checkmarx.integrations.datastore.dto.RepoDto;
import com.checkmarx.integrations.datastore.models.ScmOrg;
import com.checkmarx.integrations.datastore.models.ScmRepo;
import com.checkmarx.integrations.datastore.repositories.ScmRepoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RepoService {

    private final ScmRepoRepository scmRepoRepository;

    public List<ScmRepo> getScmReposByOrgName(String scmBaseUrl, String orgName) {
        return scmRepoRepository.getScmReposByOrgName(scmBaseUrl, orgName);
    }

    public ScmRepo getScmRepo(String scmBaseUrl, String orgName, String repoName) {
        return scmRepoRepository.getRepo(scmBaseUrl, orgName, repoName);
    }

    public void updateScmOrgRepos(ScmOrg scmOrg, List<RepoDto> repoDtoList) {
        repoDtoList.forEach(repoDto -> {
            String repoName = repoDto.getName();

            if (isScmRepoExists(scmOrg, repoName)) {
                ScmRepo repoToUpdate = scmRepoRepository.getRepoByName(scmOrg.getName(), repoName);
                repoToUpdate.setWebhookId(repoDto.getWebhookId());
                repoToUpdate.setWebhookConfigured(repoDto.isWebhookConfigured());
                scmRepoRepository.save(repoToUpdate);
            } else {
                ScmRepo scmRepo = ScmRepo.builder()
                        .scmOrg(scmOrg)
                        .name(repoName)
                        .webhookId(repoDto.getWebhookId())
                        .isWebhookConfigured(repoDto.isWebhookConfigured())
                        .build();
                scmRepoRepository.saveAndFlush(scmRepo);
            }
        });
    }

    private boolean isScmRepoExists(ScmOrg scmOrg, String repoName) {
        return scmRepoRepository.getRepoByName(scmOrg.getName(), repoName) != null;
    }
}
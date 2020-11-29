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

    public List<ScmRepo> getScmReposByOrgIdentity(String scmBaseUrl, String orgIdentity) {
        return scmRepoRepository.getScmReposByOrgIdentity(scmBaseUrl, orgIdentity);
    }

    public ScmRepo getScmRepo(String scmBaseUrl, String orgIdentity, String repoIdentity) {
        return scmRepoRepository.getRepo(scmBaseUrl, orgIdentity, repoIdentity);
    }

    public void updateScmOrgRepos(ScmOrg scmOrg, List<RepoDto> repoDtoList) {
        repoDtoList.forEach(repoDto -> {
            String repoIdentity = repoDto.getRepoIdentity();

            if (isScmRepoExists(scmOrg, repoIdentity)) {
                ScmRepo repoToUpdate = scmRepoRepository.getRepoByIdentity(scmOrg.getOrgIdentity(), repoIdentity);
                repoToUpdate.setWebhookId(repoDto.getWebhookId());
                repoToUpdate.setWebhookConfigured(repoDto.isWebhookConfigured());
                scmRepoRepository.save(repoToUpdate);
            } else {
                ScmRepo scmRepo = ScmRepo.builder()
                        .scmOrg(scmOrg)
                        .repoIdentity(repoIdentity)
                        .webhookId(repoDto.getWebhookId())
                        .isWebhookConfigured(repoDto.isWebhookConfigured())
                        .build();
                scmRepoRepository.saveAndFlush(scmRepo);
            }
        });
    }

    private boolean isScmRepoExists(ScmOrg scmOrg, String repoIdentity) {
        return scmRepoRepository.getRepoByIdentity(scmOrg.getOrgIdentity(), repoIdentity) != null;
    }
}
package com.checkmarx.integrations.datastore.services;

import com.checkmarx.integrations.datastore.dto.RepoDto;
import com.checkmarx.integrations.datastore.models.ScmOrg;
import com.checkmarx.integrations.datastore.models.ScmRepo;
import com.checkmarx.integrations.datastore.repositories.ScmRepoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
        List<ScmRepo> scmRepoList = new ArrayList<>();

        repoDtoList.forEach(repoDto -> {
            ScmRepo scmRepo = ScmRepo.builder()
                    .scmOrg(scmOrg)
                    .name(repoDto.getName())
                    .isWebhookConfigured(repoDto.isWebhookConfigured())
                    .build();
            if (!isScmRepoExists(scmOrg, scmRepo)) {
                scmRepoList.add(scmRepo);
            }
        });
        scmRepoRepository.saveAll(scmRepoList);
    }

    private boolean isScmRepoExists(ScmOrg scmOrg, ScmRepo scmRepo) {
        return scmRepoRepository.getRepoByName(scmOrg.getName(), scmRepo.getName()) != null;
    }
}
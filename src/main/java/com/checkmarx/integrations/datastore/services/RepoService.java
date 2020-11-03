package com.checkmarx.integrations.datastore.services;

import com.checkmarx.integrations.datastore.models.ScmRepo;
import com.checkmarx.integrations.datastore.repositories.ScmRepoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RepoService {

    private final ScmRepoRepository scmRepoRepository;

    public ScmRepo getRepoBy(String scmBaseUrl, String orgName, String repo) {
        return scmRepoRepository.getRepo(repo, orgName, scmBaseUrl);
    }
}
package com.checkmarx.integrations.datastore.services;

import com.checkmarx.integrations.datastore.models.ScmRepo;
import com.checkmarx.integrations.datastore.repositories.ScmRepoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RepoService {

    @Autowired
    private ScmRepoRepository scmRepoRepository;

    public List<ScmRepo> getRepoBy(Long scmId, String nameSpace, String repo) {
        return scmRepoRepository.getRepo(repo, nameSpace, scmId);
    }
}
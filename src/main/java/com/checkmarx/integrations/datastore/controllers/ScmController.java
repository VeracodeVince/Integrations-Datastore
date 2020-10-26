package com.checkmarx.integrations.datastore.controllers;

import com.checkmarx.integrations.datastore.models.Scm;
import com.checkmarx.integrations.datastore.models.ScmOrg;
import com.checkmarx.integrations.datastore.models.ScmRepo;
import com.checkmarx.integrations.datastore.repositories.ScmOrgRepository;
import com.checkmarx.integrations.datastore.repositories.ScmRepoRepository;
import com.checkmarx.integrations.datastore.repositories.ScmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ScmController {

    @Autowired
    private ScmRepository scmRepository;

    @Autowired
    private ScmOrgRepository scmOrgRepository;

    @Autowired
    private ScmRepoRepository scmRepoRepository;

    @GetMapping("/scms")
    public List<Scm> scmList() {
        return scmRepository.findAll();
    }

    @GetMapping("/scmOrgs")
    public List<ScmOrg> scmOrgsList() {
        return scmOrgRepository.findAll();
    }

    @PostMapping("/scmOrgs")
    public ScmOrg createScmOrg(@RequestBody final ScmOrg scmOrg) {
        return scmOrgRepository.saveAndFlush(scmOrg);
    }

    @GetMapping("/scmRepos")
    public List<ScmRepo> scmReposList() {
        return scmRepoRepository.findAll();
    }

}
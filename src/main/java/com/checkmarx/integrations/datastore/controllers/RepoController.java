package com.checkmarx.integrations.datastore.controllers;

import com.checkmarx.integrations.datastore.models.ScmRepo;
import com.checkmarx.integrations.datastore.services.RepoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/repositories")
@Slf4j
public class RepoController {

    @Autowired
    private RepoService repoService;

    @GetMapping()
    public List<ScmRepo> getScmRepo(@RequestParam Long scmId, @RequestParam("name-space") String nameSpace, @RequestParam String repo) {
        log.trace("getScmRepo: scmId={}, name-space={}, repo={}", scmId, nameSpace, repo);
        return repoService.getRepoBy(scmId, nameSpace, repo);
    }

}
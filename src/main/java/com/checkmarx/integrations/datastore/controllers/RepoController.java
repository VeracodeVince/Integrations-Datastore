package com.checkmarx.integrations.datastore.controllers;

import com.checkmarx.integrations.datastore.models.ScmRepo;
import com.checkmarx.integrations.datastore.services.RepoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/repositories")
@RequiredArgsConstructor
@Slf4j
public class RepoController {

    private final RepoService repoService;

    @GetMapping()
    public ScmRepo getScmRepo(@RequestParam String scmName, @RequestParam String orgName, @RequestParam String repo) {
        log.trace("getScmRepo: scmName={}, orgName={}, repo={}", scmName, orgName, repo);
        return repoService.getRepoBy(scmName, orgName, repo);
    }

}
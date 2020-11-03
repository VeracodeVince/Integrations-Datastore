package com.checkmarx.integrations.datastore.controllers;

import com.checkmarx.integrations.datastore.models.ScmRepo;
import com.checkmarx.integrations.datastore.services.RepoService;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "Gets a SCM repo by SCM base URL, org name & repo name")
    @GetMapping
    public ScmRepo getScmRepo(@RequestParam String scmBaseUrl, @RequestParam String orgName, @RequestParam String repo) {
        log.trace("getScmRepo: scmBaseUrl={}, orgName={}, repo={}", scmBaseUrl, orgName, repo);
        return repoService.getRepoBy(scmBaseUrl, orgName, repo);
    }

}
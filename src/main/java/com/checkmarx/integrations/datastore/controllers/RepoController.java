package com.checkmarx.integrations.datastore.controllers;

import com.checkmarx.integrations.datastore.controllers.exceptions.RepoNotFoundException;
import com.checkmarx.integrations.datastore.dto.RepoDto;
import com.checkmarx.integrations.datastore.dto.SCMRepoDto;
import com.checkmarx.integrations.datastore.models.Scm;
import com.checkmarx.integrations.datastore.models.ScmOrg;
import com.checkmarx.integrations.datastore.models.ScmRepo;
import com.checkmarx.integrations.datastore.services.OrgService;
import com.checkmarx.integrations.datastore.services.RepoService;
import com.checkmarx.integrations.datastore.services.ScmService;
import com.checkmarx.integrations.datastore.utils.ErrorMessagesHelper;
import com.checkmarx.integrations.datastore.utils.ObjectMapperUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/repos")
@RequiredArgsConstructor
@Slf4j
public class RepoController {

    private final RepoService repoService;
    private final ScmService scmService;
    private final OrgService orgService;

    @Operation(summary = "Gets a SCM org repos")
    @GetMapping
    public List<RepoDto> getScmReposByOrgName(@RequestParam String scmBaseUrl, @RequestParam String orgName) {
        log.trace("getScmReposByOrg: scmBaseUrl={}, orgName={}", scmBaseUrl, orgName);
        List<ScmRepo> scmRepoList = repoService.getScmReposByOrgName(scmBaseUrl, orgName);

        return ObjectMapperUtil.mapList(scmRepoList, RepoDto.class);
    }

    @Operation(summary = "Gets SCM repo by name")
    @GetMapping(value = "{repoName}")
    @ApiResponse(responseCode = "200", description = "Repo found", content = @Content)
    @ApiResponse(responseCode = "404", description = "Repo not found", content = @Content)
    public ResponseEntity<RepoDto> getScmRepo(@RequestParam String scmBaseUrl, @RequestParam String orgName, @PathVariable String repoName) {
        log.trace("getScmRepo: scmBaseUrl={}, orgName={}, repoName={}", scmBaseUrl, orgName, repoName);
        ScmRepo scmRepo = Optional.ofNullable(repoService.getScmRepo(scmBaseUrl, orgName, repoName))
                .orElseThrow(() -> new RepoNotFoundException(String.format(ErrorMessagesHelper.REPO_NOT_FOUND, repoName)));

        return ResponseEntity.ok(ObjectMapperUtil.map(scmRepo, RepoDto.class));
    }

    @Operation(summary = "Stores SCM repo")
    @PostMapping
    public ResponseEntity storeScmRepo(@RequestBody SCMRepoDto scmRepoDto) {
        log.trace("storeScmRepo: scmRepoDto={}", scmRepoDto);
        ScmOrg scmOrgByName = createOrGetScmOrgByScmUrl(scmRepoDto);
        repoService.createScmOrgRepos(scmOrgByName, scmRepoDto.getRepoList());

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Updates SCM repo Webhook configuration")
    @PutMapping
    public ResponseEntity updateScmRepo(@RequestBody SCMRepoDto scmRepoDto) {
        log.trace("updateScmRepo: scmRepoDto={}", scmRepoDto);
        ScmOrg scmOrgByName = createOrGetScmOrgByScmUrl(scmRepoDto);
        repoService.updateScmOrgRepos(scmOrgByName, scmRepoDto.getRepoList());

        return ResponseEntity.ok().build();
    }

    private ScmOrg createOrGetScmOrgByScmUrl(@RequestBody SCMRepoDto scmRepoDto) {
        Scm scmByBaseUrl = scmService.createOrGetScmByBaseUrl(scmRepoDto.getScmUrl());
        log.trace("createOrGetScmByBaseUrl: Scm:{}", scmByBaseUrl);
        ScmOrg scmOrgByName = orgService.createOrGetScmOrgByName(scmByBaseUrl, scmRepoDto.getOrgName());
        log.trace("createOrGetScmOrgByName: scmOrgByName:{}", scmOrgByName);
        return scmOrgByName;
    }
}
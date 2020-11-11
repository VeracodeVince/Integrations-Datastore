package com.checkmarx.integrations.datastore.controllers;

import com.checkmarx.integrations.datastore.controllers.exceptions.RepoNotFoundException;
import com.checkmarx.integrations.datastore.dto.RepoDto;
import com.checkmarx.integrations.datastore.dto.SCMRepoDto;
import com.checkmarx.integrations.datastore.models.ScmOrg;
import com.checkmarx.integrations.datastore.models.ScmRepo;
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

    @Operation(summary = "Gets a SCM org repos")
    @GetMapping
    public List<RepoDto> getScmReposByOrgName(@RequestParam String scmBaseUrl, @RequestParam String orgName) {
        log.trace("getScmReposByOrgName: scmBaseUrl={}, orgName={}", scmBaseUrl, orgName);
        List<ScmRepo> scmRepoList = repoService.getScmReposByOrgName(scmBaseUrl, orgName);

        List<RepoDto> repoDtoList = ObjectMapperUtil.mapList(scmRepoList, RepoDto.class);
        log.trace("getScmReposByOrgName: repoDtoList:{}", repoDtoList);

        return repoDtoList;
    }

    @Operation(summary = "Gets SCM repo by name")
    @GetMapping(value = "{repoName}")
    @ApiResponse(responseCode = "200", description = "Repo found", content = @Content)
    @ApiResponse(responseCode = "404", description = "Repo not found", content = @Content)
    public ResponseEntity<RepoDto> getScmRepo(@RequestParam String scmBaseUrl, @RequestParam String orgName, @PathVariable String repoName) {
        log.trace("getScmRepo: scmBaseUrl={}, orgName={}, repoName={}", scmBaseUrl, orgName, repoName);
        ScmRepo scmRepo = Optional.ofNullable(repoService.getScmRepo(scmBaseUrl, orgName, repoName))
                .orElseThrow(() -> new RepoNotFoundException(String.format(ErrorMessagesHelper.REPO_NOT_FOUND, repoName)));

        RepoDto repoDto = ObjectMapperUtil.map(scmRepo, RepoDto.class);
        log.trace("getScmRepo: repoDto:{}", repoDto);

        return ResponseEntity.ok(repoDto);
    }

    @Operation(summary = "Stores or updates SCM repos")
    @PutMapping
    public ResponseEntity updateScmRepo(@RequestBody SCMRepoDto scmRepoDto) {
        log.trace("updateScmRepo: scmRepoDto={}", scmRepoDto.toString());
        ScmOrg scmOrgByName = scmService.createOrGetScmOrgByScmUrl(scmRepoDto.getScmUrl(), scmRepoDto.getOrgName());
        log.trace("updateScmRepo: scmOrgByName={}", scmOrgByName);
        repoService.updateScmOrgRepos(scmOrgByName, scmRepoDto.getRepoList());

        return ResponseEntity.ok().build();
    }
}
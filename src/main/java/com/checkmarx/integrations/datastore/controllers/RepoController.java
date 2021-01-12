package com.checkmarx.integrations.datastore.controllers;

import com.checkmarx.integrations.datastore.controllers.exceptions.RepoNotFoundException;
import com.checkmarx.integrations.datastore.dto.RepoDto;
import com.checkmarx.integrations.datastore.dto.RepoUpdateDto;
import com.checkmarx.integrations.datastore.dto.SCMRepoDto;
import com.checkmarx.integrations.datastore.models.Scm;
import com.checkmarx.integrations.datastore.models.ScmOrg;
import com.checkmarx.integrations.datastore.models.ScmRepo;
import com.checkmarx.integrations.datastore.services.OrgService;
import com.checkmarx.integrations.datastore.services.RepoService;
import com.checkmarx.integrations.datastore.services.ScmService;
import com.checkmarx.integrations.datastore.utils.ErrorMessages;
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
@RequiredArgsConstructor
@Slf4j
public class RepoController {

    private final RepoService repoService;
    private final ScmService scmService;
    private final OrgService orgService;

    @Operation(summary = "Gets a SCM org repos")
    @GetMapping("/repos")
    public List<RepoDto> getScmReposByOrgIdentity(@RequestParam String scmBaseUrl, @RequestParam String orgIdentity) {
        log.trace("getScmReposByOrgIdentity: scmBaseUrl={}, orgIdentity={}", scmBaseUrl, orgIdentity);
        List<ScmRepo> scmRepoList = repoService.getScmReposByOrgIdentity(scmBaseUrl, orgIdentity);

        List<RepoDto> repoDtoList = ObjectMapperUtil.mapList(scmRepoList, RepoDto.class);
        log.trace("getScmReposByOrgIdentity: repoDtoList:{}", repoDtoList);

        return repoDtoList;
    }

    @Operation(summary = "Gets SCM repo by repo identity")
    @GetMapping(value = "/repos/{repoIdentity}")
    @ApiResponse(responseCode = "200", description = "SCM Repo found", content = @Content)
    @ApiResponse(responseCode = "404", description = "SCM Repo was not found", content = @Content)
    public ResponseEntity<RepoDto> getScmRepo(@RequestParam String scmBaseUrl, @RequestParam String orgIdentity, @PathVariable String repoIdentity) {
        log.trace("getScmRepo: scmBaseUrl={}, orgIdentity={}, repoIdentity={}", scmBaseUrl, orgIdentity, repoIdentity);
        ScmRepo scmRepo = Optional.ofNullable(repoService.getScmRepo(scmBaseUrl, orgIdentity, repoIdentity))
                .orElseThrow(() -> new RepoNotFoundException(String.format(ErrorMessages.REPO_NOT_FOUND, repoIdentity)));

        RepoDto repoDto = ObjectMapperUtil.map(scmRepo, RepoDto.class);
        log.trace("getScmRepo: repoDto:{}", repoDto);

        return ResponseEntity.ok(repoDto);
    }

    @Operation(summary = "Stores or updates SCM repos")
    @PutMapping("/repos")
    public ResponseEntity<Object> updateScmRepos(@RequestBody SCMRepoDto scmRepoDto) {
        log.trace("updateScmRepos: scmRepoDto={}", scmRepoDto.toString());
        Scm scm = scmService.getScmByScmUrl(scmRepoDto.getScmUrl());
        ScmOrg scmOrg = orgService.createOrGetScmOrgByOrgIdentity(scm, scmRepoDto.getOrgIdentity());
        log.trace("updateScmRepos: scmOrgByName={}", scmOrg);
        repoService.updateScmOrgRepos(scmOrg, scmRepoDto.getRepoList());

        return ResponseEntity.ok().build();
    }

    @PutMapping("scms/{scmBaseUrl}/orgs/{orgIdentity}/repos/{repoIdentity}")
    public ResponseEntity<Object> updateRepo(@PathVariable String scmBaseUrl,
                                             @PathVariable String orgIdentity,
                                             @PathVariable String repoIdentity,
                                             @RequestBody RepoUpdateDto repo) {
        log.trace("updateRepo: scmBaseUrl: {}, orgIdentity: {}, repoIdentity: {}, new property values: {}",
                scmBaseUrl, orgIdentity, repoIdentity, repo);

        repoService.updateRepo(scmBaseUrl, orgIdentity, repoIdentity, repo);

        return ResponseEntity.noContent().build();
    }
}
package com.checkmarx.integrations.datastore.controllers;

import com.checkmarx.integrations.datastore.dto.RepoDto;
import com.checkmarx.integrations.datastore.dto.RepoUpdateDto;
import com.checkmarx.integrations.datastore.dto.ReposUpdateDto;
import com.checkmarx.integrations.datastore.services.RepoService;
import com.checkmarx.integrations.datastore.services.StorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RepoController {
    private final RepoService repoService;
    private final StorageService storageService;

    @Operation(summary = "Gets organization repositories.")
    @GetMapping("scms/{scmId}/orgs/{orgIdentity}/repos")
    public List<RepoDto> getOrganizationRepos(@PathVariable long scmId, @PathVariable String orgIdentity) {
        log.trace("getOrganizationRepos: scmId={}, orgIdentity={}", scmId, orgIdentity);
        List<RepoDto> repos = repoService.getOrganizationRepos(scmId, orgIdentity);
        log.trace("getOrganizationRepos: repoDtoList:{}", repos);

        return repos;
    }

    @Operation(summary = "Gets SCM repo by repo identity.")
    @GetMapping(value = "scms/{scmId}/orgs/{orgIdentity}/repos/{repoIdentity}")
    @ApiResponse(responseCode = "200", description = "SCM repo found", content = @Content)
    @ApiResponse(responseCode = "404", description = "SCM repo was not found", content = @Content)
    public ResponseEntity<RepoDto> getRepo(@PathVariable long scmId,
                                           @PathVariable String orgIdentity,
                                           @PathVariable String repoIdentity) {
        log.trace("getRepo: scmId={}, orgIdentity={}, repoIdentity={}", scmId, orgIdentity, repoIdentity);
        RepoDto repoDto = repoService.getScmRepo(scmId, orgIdentity, repoIdentity);
        log.trace("getRepo: repoDto:{}", repoDto);

        return ResponseEntity.ok(repoDto);
    }

    @Operation(summary = "Imports an organization together with its repositories.",
            description = "If an organization doesn't exist, it is created. For each of the repos in request, " +
                    "if a repo with the same identity doesn't exist, it is created.")
    @PutMapping("/repos")
    public ResponseEntity<Void> importOrgAndRepos(@RequestBody ReposUpdateDto updateRequest) {
        log.trace("importOrgAndRepos: updateRequest={}", updateRequest);
        storageService.importOrgAndRepos(updateRequest);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Updates an SCM repository.")
    @PutMapping("scms/{scmId}/orgs/{orgIdentity}/repos/{repoIdentity}")
    public ResponseEntity<Void> updateRepo(@PathVariable long scmId,
                                             @PathVariable String orgIdentity,
                                             @PathVariable String repoIdentity,
                                             @RequestBody RepoUpdateDto repo) {
        log.trace("updateRepo: scmId: {}, orgIdentity: {}, repoIdentity: {}, new property values: {}",
                scmId, orgIdentity, repoIdentity, repo);

        repoService.updateRepo(scmId, orgIdentity, repoIdentity, repo);

        return ResponseEntity.noContent().build();
    }
}
package com.checkmarx.integrations.datastore.controllers;

import com.checkmarx.integrations.datastore.controllers.exceptions.EntityNotFoundException;
import com.checkmarx.integrations.datastore.dto.RepoDto;
import com.checkmarx.integrations.datastore.dto.RepoUpdateDto;
import com.checkmarx.integrations.datastore.dto.ReposUpdateDto;
import com.checkmarx.integrations.datastore.models.ScmRepo;
import com.checkmarx.integrations.datastore.services.RepoService;
import com.checkmarx.integrations.datastore.services.StorageService;
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
    private final StorageService storageService;

    @Operation(summary = "Gets organization repositories.")
    @GetMapping("scms/{scmId}/orgs/{orgIdentity}/repos")
    public List<RepoDto> getScmReposByOrgIdentity(@PathVariable long scmId, @PathVariable String orgIdentity) {
        log.trace("getScmReposByOrgIdentity: scmId={}, orgIdentity={}", scmId, orgIdentity);
        List<ScmRepo> repos = repoService.getScmReposByOrgIdentity(scmId, orgIdentity);

        List<RepoDto> repoDtoList = ObjectMapperUtil.mapList(repos, RepoDto.class);
        log.trace("getScmReposByOrgIdentity: repoDtoList:{}", repoDtoList);

        return repoDtoList;
    }

    @Operation(summary = "Gets SCM repo by repo identity.")
    @GetMapping(value = "scms/{scmId}/orgs/{orgIdentity}/repos/{repoIdentity}")
    @ApiResponse(responseCode = "200", description = "SCM repo found", content = @Content)
    @ApiResponse(responseCode = "404", description = "SCM repo was not found", content = @Content)
    public ResponseEntity<RepoDto> getScmRepo(@PathVariable long scmId,
                                              @PathVariable String orgIdentity,
                                              @PathVariable String repoIdentity) {
        log.trace("getScmRepo: scmId={}, orgIdentity={}, repoIdentity={}", scmId, orgIdentity, repoIdentity);
        ScmRepo scmRepo = Optional.ofNullable(repoService.getScmRepo(scmId, orgIdentity, repoIdentity))
                .orElseThrow(() -> new EntityNotFoundException(String.format(ErrorMessages.REPO_NOT_FOUND, repoIdentity)));

        RepoDto repoDto = ObjectMapperUtil.map(scmRepo, RepoDto.class);
        log.trace("getScmRepo: repoDto:{}", repoDto);

        return ResponseEntity.ok(repoDto);
    }

    @Operation(summary = "Imports an organization together with its repositories.",
            description = "If an organization doesn't exist, it is created. For each of the repos in request, " +
                    "if a repo with the same identity doesn't exist, it is created.")
    @PutMapping("/repos")
    public ResponseEntity<Void> importOrgAndRepos(@RequestBody ReposUpdateDto updateRequest) {
        log.trace("importOrgAndRepos: updateRequest={}", updateRequest);
        storageService.importOrgAndRepos(updateRequest);

        return ResponseEntity.ok().build();
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
package com.checkmarx.integrations.datastore.controllers;

import com.checkmarx.integrations.datastore.dto.SCMOrgDto;
import com.checkmarx.integrations.datastore.dto.SCMOrgShortDto;
import com.checkmarx.integrations.datastore.dto.SCMOrgUpdateDto;
import com.checkmarx.integrations.datastore.models.ScmOrg;
import com.checkmarx.integrations.datastore.services.OrgService;
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
public class OrgController {
    private final OrgService orgService;

    @Operation(summary = "Imports multiple organizations.",
            description = "For each of the organizations in request, if the organization doesn't exist, it is created; " +
                    "if the organization exists, only its access token ID is updated.")
    @PutMapping(value = "scms/{scmId}/orgs")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void importOrganizations(@PathVariable long scmId, @RequestBody List<SCMOrgShortDto> orgs) {
        log.trace("importOrganizations: SCM id: {}, organization count: {}", scmId, orgs.size());
        orgService.importOrgsIntoStorage(orgs, scmId);
    }

    @Operation(summary = "Gets an organization by its identity.")
    @GetMapping(value = "scms/{scmId}/orgs/{orgIdentity}")
    @ApiResponse(responseCode = "200", description = "Organization found", content = @Content)
    @ApiResponse(responseCode = "404", description = "Organization was not found", content = @Content)
    public SCMOrgDto getOrganization(@PathVariable long scmId, @PathVariable String orgIdentity) {
        log.trace("getOrganization: scmId={}, orgIdentity={}", scmId, orgIdentity);

        SCMOrgDto result = orgService.getOrgOrThrow(scmId, orgIdentity);
        log.trace("getOrganization: result={}", result);

        return result;
    }

    @GetMapping("orgs")
    @Operation(summary = "Find an organization by its identity and a base repo URL.",
    description = "Base repo URL is a hostname+authority part of URLs that are used for 'git clone' " +
            "in a specific SCM. E.g. https://example.com:8080")
    public SCMOrgDto getOrganizationByRepoBaseUrl(@RequestParam String orgIdentity, @RequestParam String repoBaseUrl) {
        log.trace("getOrganizationByRepoBaseUrl: orgIdentity: {}, repoBaseUrl: {}", orgIdentity, repoBaseUrl);
        SCMOrgDto result = orgService.getOrgByRepoBaseUrl(orgIdentity, repoBaseUrl);
        log.trace("Organization found: {}.", result);
        return result;
    }

    @Operation(summary = "Updates a subset of organization properties.",
            description = "If the organization doesn't exist, it is created.")
    @PostMapping(value = "scms/{scmId}/orgs/{orgIdentity}")
    public ResponseEntity<Void> storeOrganization(@PathVariable long scmId,
                                                    @PathVariable String orgIdentity,
                                                    @RequestBody SCMOrgUpdateDto updateRequest) {
        log.trace("storeOrganization: scmId={}, orgIdentity={}, updateRequest={}", scmId, orgIdentity, updateRequest);

        ScmOrg scmOrg = orgService.createOrgIfDoesntExist(scmId, orgIdentity);
        orgService.updateOrg(scmOrg, updateRequest);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Deletes an organization")
    @DeleteMapping(value = "orgs/{id}")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void deleteOrganization(@PathVariable Long id) {
        log.trace("deleteOrganization: id={}", id);
        orgService.deleteScmOrgById(id);
    }
}
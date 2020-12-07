package com.checkmarx.integrations.datastore.controllers;

import com.checkmarx.integrations.datastore.controllers.exceptions.ScmOrgNotFoundException;
import com.checkmarx.integrations.datastore.dto.CxFlowPropertiesDto;
import com.checkmarx.integrations.datastore.dto.SCMOrgDto;
import com.checkmarx.integrations.datastore.models.ScmOrg;
import com.checkmarx.integrations.datastore.services.OrgService;
import com.checkmarx.integrations.datastore.services.ScmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static com.checkmarx.integrations.datastore.utils.ErrorConstsMessages.BASE_URL_WITH_ORG_NOT_FOUND;

@RestController
@RequestMapping("/orgs")
@RequiredArgsConstructor
@Slf4j
public class ScmOrgController {

    private final OrgService orgService;
    private final ScmService scmService;

    @Operation(summary = "Stores a SCM org")
    @PostMapping
    public ResponseEntity storeScmOrg(@RequestBody final SCMOrgDto scmOrgDto) {
        log.trace("storeScmOrg: scmOrgDto={}", scmOrgDto);
        ScmOrg scmOrg = scmService.getScmOrgByScmUrlAndOrgIdentity(scmOrgDto.getScmUrl(), scmOrgDto.getOrgIdentity());
        log.trace("storeScmOrg: scmOrg={}", scmOrg);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Gets SCM org with Cx-Flow properties")
    @GetMapping(value = "/properties")
    @ApiResponse(responseCode = "200", description = "Cx-Flow properties found", content = @Content)
    @ApiResponse(responseCode = "404", description = "Cx-Flow properties were not found", content = @Content)
    public CxFlowPropertiesDto getCxFlowProperties(@RequestParam String scmBaseUrl, @RequestParam String orgIdentity) {
        log.trace("getCxFlowProperties: scmBaseUrl={}, orgIdentity={}", scmBaseUrl, orgIdentity);
        ScmOrg scmOrg = Optional.ofNullable(orgService.getOrgBy(scmBaseUrl, orgIdentity))
                .orElseThrow(() -> new ScmOrgNotFoundException(String.format(BASE_URL_WITH_ORG_NOT_FOUND, scmBaseUrl, orgIdentity)));

        CxFlowPropertiesDto cxFlowPropertiesDto =
                CxFlowPropertiesDto.builder()
                        .scmUrl(scmOrg.getScm().getBaseUrl())
                        .cxGoToken(scmOrg.getCxGoToken())
                        .cxTeam(scmOrg.getTeam())
                        .cxFlowUrl(scmOrg.getCxFlowUrl())
                        .orgIdentity(scmOrg.getOrgIdentity())
                        .build();
        log.trace("getCxFlowProperties: cxFlowPropertiesDto={}", cxFlowPropertiesDto);

        return cxFlowPropertiesDto;
    }

    @Operation(summary = "Stores SCM org with Cx-Flow properties")
    @PostMapping(value = "/properties")
    public ScmOrg storeCxFlowProperties(@RequestBody final CxFlowPropertiesDto cxFlowPropertiesDto) {
        log.trace("storeCxFlowProperties: cxFlowPropertiesDto={}", cxFlowPropertiesDto);
        ScmOrg scmOrg = scmService.getScmOrgByScmUrlAndOrgIdentity(cxFlowPropertiesDto.getScmUrl(), cxFlowPropertiesDto.getOrgIdentity());
        log.trace("storeCxFlowProperties: scmOrg={}", scmOrg);
        orgService.updateCxFlowProperties(scmOrg, cxFlowPropertiesDto);

        return scmOrg;
    }

    @Operation(summary = "Deletes a SCM org")
    @DeleteMapping(value = "{id}")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void deleteScmOrg(@PathVariable Long id) {
        log.trace("deleteScmOrg: id={}", id);
        orgService.deleteScmOrgById(id);
    }
}
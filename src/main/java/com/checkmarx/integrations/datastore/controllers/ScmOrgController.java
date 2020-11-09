package com.checkmarx.integrations.datastore.controllers;

import com.checkmarx.integrations.datastore.dto.CxFlowPropertiesDto;
import com.checkmarx.integrations.datastore.models.ScmOrg;
import com.checkmarx.integrations.datastore.services.OrgService;
import com.checkmarx.integrations.datastore.services.ScmService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orgs")
@RequiredArgsConstructor
@Slf4j
public class ScmOrgController {

    private final OrgService orgService;
    private final ScmService scmService;

    @Operation(summary = "Gets a SCM org")
    @GetMapping()
    public ScmOrg getScmOrg(@RequestParam String scmBaseUrl, @RequestParam String orgName) {
        log.trace("getScmOrg: scmBaseUrl={}, orgName={}", scmBaseUrl, orgName);

        return orgService.getOrgBy(scmBaseUrl, orgName);
    }

    @Operation(summary = "Stores a SCM org")
    @PostMapping
    public ScmOrg storeScmOrg(@RequestBody final ScmOrg scmOrg) {
        log.trace("storeScmOrg: scmOrg={}", scmOrg);

        return orgService.createScmOrg(scmOrg);
    }

    @Operation(summary = "Stores SCM org with Cx-Flow properties")
    @PostMapping(value = "/properties")
    public ScmOrg storeCxFlowProperties(@RequestBody final CxFlowPropertiesDto cxFlowPropertiesDto) {
        log.trace("storeCxFlowProperties: cxFlowPropertiesDto={}", cxFlowPropertiesDto);
        ScmOrg scmOrg = scmService.createOrGetScmOrgByScmUrl(cxFlowPropertiesDto.getScmUrl(), cxFlowPropertiesDto.getOrgName());
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
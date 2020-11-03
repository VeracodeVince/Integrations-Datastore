package com.checkmarx.integrations.datastore.controllers;

import com.checkmarx.integrations.datastore.models.ScmOrg;
import com.checkmarx.integrations.datastore.services.OrgService;
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

    @Operation(summary = "Gets a SCM org by SCM name & org name")
    @GetMapping()
    public ScmOrg getScmOrg(@RequestParam String scmName, @RequestParam String orgName) {
        log.trace("getScmOrg: scmName={}, orgName={}", scmName, orgName);
        return orgService.getOrgBy(scmName, orgName);
    }

    @Operation(summary = "Creates a SCM org")
    @PostMapping
    public ScmOrg createScmOrg(@RequestBody final ScmOrg scmOrg) {
        log.trace("createScmOrg: scmOrg={}", scmOrg);
        return orgService.createScmOrg(scmOrg);
    }

    @Operation(summary = "Deletes a SCM org by id")
    @DeleteMapping(value = "{id}")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void deleteScmOrg(@PathVariable Long id) {
        log.trace("deleteScmOrg: id={}", id);
        orgService.deleteScmOrgById(id);
    }
}
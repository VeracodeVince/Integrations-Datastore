package com.checkmarx.integrations.datastore.controllers;

import com.checkmarx.integrations.datastore.models.ScmOrg;
import com.checkmarx.integrations.datastore.services.OrgService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orgs")
@RequiredArgsConstructor
@Slf4j
public class ScmOrgController {

    private final OrgService orgService;

    @GetMapping()
    public ScmOrg getScmOrg(@RequestParam String scmName, @RequestParam String orgName) {
        log.trace("getScmOrg: scmName={}, orgName={}", scmName, orgName);
        return orgService.getOrgBy(scmName, orgName);
    }

    @PostMapping
    public ScmOrg createScmOrg(@RequestBody final ScmOrg scmOrg) {
        return orgService.createScmOrg(scmOrg);
    }
}
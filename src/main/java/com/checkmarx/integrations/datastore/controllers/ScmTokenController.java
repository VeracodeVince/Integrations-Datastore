package com.checkmarx.integrations.datastore.controllers;

import com.checkmarx.integrations.datastore.dto.SCMAccessTokenDto;
import com.checkmarx.integrations.datastore.models.Scm;
import com.checkmarx.integrations.datastore.models.ScmOrg;
import com.checkmarx.integrations.datastore.models.Token;
import com.checkmarx.integrations.datastore.services.OrgService;
import com.checkmarx.integrations.datastore.services.ScmService;
import com.checkmarx.integrations.datastore.services.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tokens")
@RequiredArgsConstructor
@Slf4j
public class ScmTokenController {

    private final TokenService tokenService;
    private final ScmService scmService;
    private final OrgService orgService;

    @Operation(summary = "Gets token by org name & token type")
    @GetMapping
    public Token getToken(@RequestParam String orgName, @RequestParam String type) {
        log.trace("getToken: orgName={}, type={}", orgName, type);
        return tokenService.getToken(orgName, type);
    }

    @Operation(summary = "Stores SCM access token by SCM URL & SCM org name")
    @PostMapping(value = "/storeScmAccessToken")
    public ResponseEntity storeScmAccessToken(@RequestBody SCMAccessTokenDto scmAccessTokenDto) {
        log.trace("storeScmAccessToken: scmAccessTokenDto={}", scmAccessTokenDto);
        Scm scmByBaseUrl = scmService.createOrGetScmByBaseUrl(scmAccessTokenDto.getScmUrl());
        log.trace("createOrGetScmByBaseUrl: Scm:{}", scmByBaseUrl);
        ScmOrg scmOrgByName = orgService.createOrGetScmOrgByName(scmByBaseUrl, scmAccessTokenDto.getOrgName());
        log.trace("createOrGetScmOrgByName: scmOrgByName:{}", scmOrgByName);
        tokenService.updateTokenIfExists(scmOrgByName, scmAccessTokenDto.getTokenType(), scmAccessTokenDto.getAccessToken());

        return ResponseEntity.ok().build();
    }
}
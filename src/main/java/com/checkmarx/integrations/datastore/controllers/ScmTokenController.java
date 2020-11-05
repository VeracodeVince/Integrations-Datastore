package com.checkmarx.integrations.datastore.controllers;

import com.checkmarx.integrations.datastore.controllers.exceptions.TokenNotFoundException;
import com.checkmarx.integrations.datastore.dto.SCMAccessTokenDto;
import com.checkmarx.integrations.datastore.models.Scm;
import com.checkmarx.integrations.datastore.models.ScmOrg;
import com.checkmarx.integrations.datastore.models.Token;
import com.checkmarx.integrations.datastore.services.OrgService;
import com.checkmarx.integrations.datastore.services.ScmService;
import com.checkmarx.integrations.datastore.services.TokenService;
import com.checkmarx.integrations.datastore.utils.ErrorMessagesHelper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/tokens")
@RequiredArgsConstructor
@Slf4j
public class ScmTokenController {

    private final TokenService tokenService;
    private final ScmService scmService;
    private final OrgService orgService;
    private final ModelMapper modelMapper;

    @Operation(summary = "Gets SCN access token")
    @GetMapping
    public SCMAccessTokenDto getScmAccessToken(@RequestParam String scmUrl, @RequestParam String orgName) {
        log.trace("getScmAccessToken: scmUrl={} orgName={}", scmUrl, orgName);
        Token token = Optional.ofNullable(orgService.getOrgBy(scmUrl, orgName))
                .map(oName -> tokenService.getTokenByOrgName(oName.getName()))
                .orElseThrow(() ->
                        new TokenNotFoundException(String.format(ErrorMessagesHelper.ACCESS_TOKEN_NOT_FOUND, scmUrl, orgName)));

        modelMapper.getConfiguration().setAmbiguityIgnored(true);
        return modelMapper.map(token, SCMAccessTokenDto.class);
    }

    @Operation(summary = "Stores SCM access token")
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
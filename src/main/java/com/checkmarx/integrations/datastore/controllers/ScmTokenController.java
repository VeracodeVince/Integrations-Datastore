package com.checkmarx.integrations.datastore.controllers;

import com.checkmarx.integrations.datastore.controllers.exceptions.TokenNotFoundException;
import com.checkmarx.integrations.datastore.dto.SCMAccessTokenDto;
import com.checkmarx.integrations.datastore.models.ScmOrg;
import com.checkmarx.integrations.datastore.models.Token;
import com.checkmarx.integrations.datastore.services.OrgService;
import com.checkmarx.integrations.datastore.services.ScmService;
import com.checkmarx.integrations.datastore.services.TokenService;
import com.checkmarx.integrations.datastore.utils.ErrorMessagesHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @ApiResponse(responseCode = "200", description = "Access token found", content = @Content)
    @ApiResponse(responseCode = "404", description = "Access token not found", content = @Content)
    public SCMAccessTokenDto getScmAccessToken(@RequestParam String scmUrl, @RequestParam String orgName) {
        log.trace("getScmAccessToken: scmUrl={} orgName={}", scmUrl, orgName);
        Token token = Optional.ofNullable(orgService.getOrgBy(scmUrl, orgName))
                .map(oName -> tokenService.getTokenByOrgName(oName.getName()))
                .orElseThrow(() ->
                        new TokenNotFoundException(String.format(ErrorMessagesHelper.ACCESS_TOKEN_NOT_FOUND, scmUrl, orgName)));

        return SCMAccessTokenDto.builder()
                .scmUrl(scmUrl)
                .orgName(orgName)
                .accessToken(token.getAccessToken())
                .tokenType(token.getType())
                .build();
    }

    @Operation(summary = "Stores SCM access token")
    @PostMapping(value = "/storeScmAccessToken")
    public ResponseEntity storeScmAccessToken(@RequestBody SCMAccessTokenDto scmAccessTokenDto) {
        log.trace("storeScmAccessToken: scmAccessTokenDto={}", scmAccessTokenDto);
        ScmOrg scmOrgByName = scmService.createOrGetScmOrgByScmUrl(scmAccessTokenDto.getScmUrl(), scmAccessTokenDto.getOrgName());
        tokenService.updateTokenIfExists(scmOrgByName, scmAccessTokenDto.getTokenType(), scmAccessTokenDto.getAccessToken());

        return ResponseEntity.ok().build();
    }
}
package com.checkmarx.integrations.datastore.controllers;

import com.checkmarx.integrations.datastore.controllers.exceptions.TokenNotFoundException;
import com.checkmarx.integrations.datastore.dto.SCMAccessTokenDto;
import com.checkmarx.integrations.datastore.models.ScmOrg;
import com.checkmarx.integrations.datastore.models.Token;
import com.checkmarx.integrations.datastore.services.OrgService;
import com.checkmarx.integrations.datastore.services.ScmService;
import com.checkmarx.integrations.datastore.services.TokenService;
import com.checkmarx.integrations.datastore.utils.ErrorConstsMessages;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public SCMAccessTokenDto getScmAccessToken(@RequestParam String scmUrl, @RequestParam String orgIdentity) {
        log.trace("getScmAccessToken: scmUrl={} orgIdentity={}", scmUrl, orgIdentity);
        Token token = Optional.ofNullable(orgService.getOrgBy(scmUrl, orgIdentity))
                .map(oName -> tokenService.getTokenByOrgIdentity(oName.getOrgIdentity()))
                .orElseThrow(() ->
                        new TokenNotFoundException(String.format(ErrorConstsMessages.ACCESS_TOKEN_NOT_FOUND, scmUrl, orgIdentity)));

        SCMAccessTokenDto scmAccessTokenDto = SCMAccessTokenDto.builder()
                .scmUrl(scmUrl)
                .orgIdentity(orgIdentity)
                .accessToken(token.getAccessToken())
                .tokenType(token.getType())
                .build();
        log.trace("getScmAccessToken: scmAccessTokenDto={}", scmAccessTokenDto);

        return scmAccessTokenDto;
    }

    @Operation(summary = "Stores or updates SCM access tokens")
    @PutMapping(value = "/storeScmAccessToken")
    public ResponseEntity storeScmAccessToken(@RequestBody List<SCMAccessTokenDto> scmAccessTokenDtoList) {
        log.trace("storeScmAccessToken: scmAccessTokenDtoList={}", scmAccessTokenDtoList);
        scmAccessTokenDtoList.forEach(scmAccessTokenDto -> {
            ScmOrg scmOrgByName = scmService.createOrGetScmOrgByScmUrl(scmAccessTokenDto.getScmUrl(), scmAccessTokenDto.getOrgIdentity());
            log.trace("storeScmAccessToken: scmOrgByName={}", scmOrgByName);
            tokenService.updateTokenIfExists(scmOrgByName, scmAccessTokenDto.getTokenType(), scmAccessTokenDto.getAccessToken());
        });

        return ResponseEntity.ok().build();
    }
}
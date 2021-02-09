package com.checkmarx.integrations.datastore.controllers;

import com.checkmarx.integrations.datastore.dto.AccessTokenDto;
import com.checkmarx.integrations.datastore.dto.AccessTokenShortDto;
import com.checkmarx.integrations.datastore.services.StorageService;
import com.checkmarx.integrations.datastore.services.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("tokens")
@RequiredArgsConstructor
@Slf4j
public class TokenController {
    private final TokenService tokenService;
    private final StorageService storageService;

    @Operation(summary = "Gets SCM access token for a specific organization.")
    @GetMapping
    @ApiResponse(responseCode = "200", description = "Access token found", content = @Content)
    @ApiResponse(responseCode = "404", description = "Access token was not found", content = @Content)
    public AccessTokenDto getScmAccessToken(@RequestParam long scmId, @RequestParam String orgIdentity) {
        log.trace("getScmAccessToken: scmId={} orgIdentity={}", scmId, orgIdentity);
        AccessTokenDto result = storageService.getScmAccessToken(scmId, orgIdentity);
        log.trace("getScmAccessToken: returning token with ID={}", result.getId());

        return result;
    }

    @Operation(summary = "Updates a specified SCM access token.")
    @PutMapping(value = "{id}")
    @ApiResponse(responseCode = "204", description = "Token was updated successfully", content = @Content)
    @ApiResponse(responseCode = "404", description = "Access token was not found", content = @Content)
    public ResponseEntity<Void> updateTokenInfo(@PathVariable long id, @RequestBody AccessTokenShortDto tokenInfo) {
        log.trace("updateTokenInfo: id={}", id);
        tokenService.updateTokenInfo(tokenInfo, id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Creates an SCM access token.",
            description = "Creates a token and returns its ID. If a token with exactly the same body " +
                    "already exists, returns the existing token ID.")
    @PostMapping
    @ApiResponse(responseCode = "201", description = "New or existing token ID.")
    @ResponseStatus(HttpStatus.CREATED)
    public long createTokenInfo(@RequestBody AccessTokenShortDto tokenInfo) {
        return tokenService.createTokenInfoIfDoesntExist(tokenInfo);
    }
}
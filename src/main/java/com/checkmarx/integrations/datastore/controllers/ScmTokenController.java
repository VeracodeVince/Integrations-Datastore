package com.checkmarx.integrations.datastore.controllers;

import com.checkmarx.integrations.datastore.controllers.exceptions.TokenNotFoundException;
import com.checkmarx.integrations.datastore.dto.AccessTokenCompleteDto;
import com.checkmarx.integrations.datastore.dto.AccessTokenUpdateDto;
import com.checkmarx.integrations.datastore.models.ScmOrg;
import com.checkmarx.integrations.datastore.models.Token;
import com.checkmarx.integrations.datastore.services.OrgService;
import com.checkmarx.integrations.datastore.services.TokenService;
import com.checkmarx.integrations.datastore.utils.ErrorMessages;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.function.Supplier;

@RestController
@RequestMapping("tokens")
@RequiredArgsConstructor
@Slf4j
public class ScmTokenController {

    private final TokenService tokenService;
    private final OrgService orgService;

    @Operation(summary = "Gets SCM access token")
    @GetMapping
    @ApiResponse(responseCode = "200", description = "Access token found", content = @Content)
    @ApiResponse(responseCode = "404", description = "Access token was not found", content = @Content)
    public AccessTokenCompleteDto getScmAccessToken(@RequestParam String scmUrl, @RequestParam String orgIdentity) {
        log.trace("getScmAccessToken: scmUrl={} orgIdentity={}", scmUrl, orgIdentity);

        ScmOrg org = orgService.getOrgBy(scmUrl, orgIdentity);

        AccessTokenCompleteDto result = Optional.ofNullable(org)
                .map(ScmOrg::getAccessToken)
                .map(ScmTokenController::toTokenResponse)
                .orElseThrow(notFoundException(scmUrl, orgIdentity));

        log.trace("getScmAccessToken: returning token with ID={}", result.getId());

        return result;
    }

    @Operation(summary = "Updates a specified SCM access token.")
    @PutMapping(value = "{id}")
    @ApiResponse(responseCode = "204", description = "Token was updated successfully", content = @Content)
    @ApiResponse(responseCode = "404", description = "Access token was not found", content = @Content)
    public ResponseEntity<Object> updateTokenInfo(@PathVariable long id, @RequestBody AccessTokenUpdateDto tokenInfo) {
        log.trace("updateTokenInfo: id={}", id);
        tokenService.updateTokenInfo(tokenInfo, id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Creates an SCM access token.",
            description = "Creates a token and returns its ID. If a token with exactly the same body " +
                    "already exists, returns the existing token ID.")
    @PostMapping
    public long createTokenInfo(@RequestBody AccessTokenUpdateDto tokenInfo) {
        return tokenService.createTokenInfoIfDoesntExist(tokenInfo);
    }

    private static AccessTokenCompleteDto toTokenResponse(Token tokenFromStorage) {
        return AccessTokenCompleteDto.builder()
                .id(tokenFromStorage.getId())
                .accessToken(tokenFromStorage.getAccessToken())
                .build();
    }

    private static Supplier<TokenNotFoundException> notFoundException(String scmUrl, String orgIdentity) {
        return () -> {
            String message = String.format(ErrorMessages.ACCESS_TOKEN_NOT_FOUND, scmUrl, orgIdentity);
            return new TokenNotFoundException(message);
        };
    }
}
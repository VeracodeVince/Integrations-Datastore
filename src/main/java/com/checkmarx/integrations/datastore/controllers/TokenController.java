package com.checkmarx.integrations.datastore.controllers;

import com.checkmarx.integrations.datastore.models.Token;
import com.checkmarx.integrations.datastore.services.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class TokenController {

    private final TokenService tokenService;

    @GetMapping
    public Token getToken(@RequestParam String orgName, @RequestParam String type) {
        log.trace("getToken: orgName={}, type={}", orgName, type);
        return tokenService.getTokens(orgName, type);
    }

    @PostMapping
    public Token addToken(@RequestBody Token token) {
        log.trace("addToken: token={}", token);
        return tokenService.addToken(token);
    }
}
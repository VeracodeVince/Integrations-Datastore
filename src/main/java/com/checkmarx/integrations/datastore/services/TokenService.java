package com.checkmarx.integrations.datastore.services;

import java.util.List;

import com.checkmarx.integrations.datastore.models.Token;
import com.checkmarx.integrations.datastore.repositories.ScmTokenRepository;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final ScmTokenRepository scmTokenRepository;

    public List<Token> getTokens() {
        return scmTokenRepository.findAll();
    }
}
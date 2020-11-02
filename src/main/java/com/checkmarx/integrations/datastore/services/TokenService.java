package com.checkmarx.integrations.datastore.services;

import com.checkmarx.integrations.datastore.models.Token;
import com.checkmarx.integrations.datastore.repositories.ScmTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final ScmTokenRepository scmTokenRepository;

    public Token getTokens(String orgName, String type) {
        return scmTokenRepository.getToken(orgName, type);
    }

	public Token addToken(Token token) {
		return scmTokenRepository.saveAndFlush(token);
	}
}
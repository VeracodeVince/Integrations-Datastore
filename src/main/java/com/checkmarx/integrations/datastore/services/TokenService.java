package com.checkmarx.integrations.datastore.services;

import com.checkmarx.integrations.datastore.models.ScmOrg;
import com.checkmarx.integrations.datastore.models.Token;
import com.checkmarx.integrations.datastore.repositories.ScmTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final ScmTokenRepository scmTokenRepository;

    public Token getTokenByOrgName(String orgName) {
        return scmTokenRepository.getTokenByOrgName(orgName);
    }

    public void updateTokenIfExists(ScmOrg scmOrg, String type, String rawToken) {
        Token token = getToken(scmOrg.getName(), type);
        if (token != null) {
            token.setAccessToken(rawToken);
            scmTokenRepository.save(token);
        } else {
            createTokenByScmOrg(scmOrg, type, rawToken);
        }
    }

    private Token getToken(String orgName, String type) {
        return scmTokenRepository.getToken(orgName, type);
    }

    private Token addToken(Token token) {
        return scmTokenRepository.saveAndFlush(token);
    }

    private Token createTokenByScmOrg(ScmOrg scmOrg, String type, String rawToken) {
        Token tokenToCreate = Token.builder()
                .scmOrg(scmOrg)
                .type(type)
                .accessToken(rawToken)
                .build();
        return addToken(tokenToCreate);
    }
}
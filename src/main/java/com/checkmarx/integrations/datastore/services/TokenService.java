package com.checkmarx.integrations.datastore.services;

import com.checkmarx.integrations.datastore.controllers.exceptions.TokenNotFoundException;
import com.checkmarx.integrations.datastore.dto.AccessTokenUpdateDto;
import com.checkmarx.integrations.datastore.models.Token;
import com.checkmarx.integrations.datastore.repositories.ScmTokenRepository;
import com.checkmarx.integrations.datastore.utils.ErrorMessages;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final ScmTokenRepository scmTokenRepository;

    public void updateTokenInfo(AccessTokenUpdateDto tokenInfo, long id) {
        Token repoTokenInfo = scmTokenRepository.findById(id)
                .orElseThrow(notFoundException(id));

        repoTokenInfo.setAccessToken(tokenInfo.getAccessToken());
        scmTokenRepository.saveAndFlush(repoTokenInfo);
    }

    /**
     * @return ID of an existing or a new token.
     */
    public long createTokenInfoIfDoesntExist(AccessTokenUpdateDto tokenInfo) {
        // Some SCMs may return exactly the same token in different "generate token" API responses.
        // If this is the case, there is no need to duplicate token records in the storage
        // => reusing an existing token record.
        Token existingTokenInfo = searchByTokenString(tokenInfo.getAccessToken());
        return Optional.ofNullable(existingTokenInfo)
                .map(Token::getId)
                .orElseGet(createToken(tokenInfo));
    }

    private Supplier<Long> createToken(AccessTokenUpdateDto tokenInfo) {
        return () -> {
            Token repoTokenInfo = Token.builder()
                    .accessToken(tokenInfo.getAccessToken())
                    .build();
            Token newToken = scmTokenRepository.saveAndFlush(repoTokenInfo);
            return newToken.getId();
        };
    }

    private Token searchByTokenString(String token) {
        Example<Token> havingSameTokenString = Example.of(Token.builder()
                .accessToken(token)
                .build());
        List<Token> existingTokens = scmTokenRepository.findAll(havingSameTokenString, Sort.by("id"));
        return existingTokens.stream().findFirst().orElse(null);
    }

    private static Supplier<TokenNotFoundException> notFoundException(long id) {
        return () -> new TokenNotFoundException(String.format(ErrorMessages.ACCESS_TOKEN_NOT_FOUND_BY_ID, id));
    }
}
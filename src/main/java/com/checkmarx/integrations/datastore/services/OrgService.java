package com.checkmarx.integrations.datastore.services;

import com.checkmarx.integrations.datastore.controllers.exceptions.EntityNotFoundException;
import com.checkmarx.integrations.datastore.dto.SCMOrgDto;
import com.checkmarx.integrations.datastore.dto.SCMOrgShortDto;
import com.checkmarx.integrations.datastore.dto.SCMOrgUpdateDto;
import com.checkmarx.integrations.datastore.models.Scm;
import com.checkmarx.integrations.datastore.models.ScmOrg;
import com.checkmarx.integrations.datastore.models.Token;
import com.checkmarx.integrations.datastore.repositories.ScmOrgRepository;
import com.checkmarx.integrations.datastore.repositories.ScmRepository;
import com.checkmarx.integrations.datastore.repositories.ScmTokenRepository;
import com.checkmarx.integrations.datastore.utils.ErrorMessages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrgService {
    private final ScmOrgRepository scmOrgRepository;
    private final ScmRepository scmRepository;
    private final ScmTokenRepository tokenRepository;
    private final ModelMapper modelMapper;

    public ScmOrg getOrg(long scmId, String orgIdentity) {
        return scmOrgRepository.getScmOrg(scmId, orgIdentity);
    }

    public SCMOrgDto getOrgByRepoBaseUrl(String orgIdentity, String repoBaseUrl) {
        return Optional.ofNullable(scmOrgRepository.getByRepoBaseUrl(orgIdentity, repoBaseUrl))
                .map(this::toWebDto)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(ErrorMessages.ORG_NOT_FOUND_BY_REPO, orgIdentity, repoBaseUrl)));
    }

    public void deleteOrg(long id) {
        scmOrgRepository.deleteById(id);
    }

    public void updateOrg(ScmOrg org, SCMOrgUpdateDto updateRequest) {
        log.trace("updateOrg: org ID: {}, update request: {}", org.getId(), updateRequest);

        Optional.ofNullable(updateRequest.getCxFlowConfig())
                .ifPresent(org::setCxFlowConfig);

        Optional.ofNullable(updateRequest.getTeam())
                .ifPresent(org::setTeam);

        scmOrgRepository.saveAndFlush(org);

        log.trace("updateOrg: updated successfully.");
    }

    public ScmOrg createOrgIfDoesntExist(long scmId, String orgIdentity) {
        ScmOrg existingOrg = getOrg(scmId, orgIdentity);

        if (existingOrg != null) {
            log.trace("createOrgIfDoesntExist: org exists: {}", existingOrg);
            return existingOrg;
        } else {
            return createOrg(scmId, orgIdentity);
        }
    }

    public void importOrgsIntoStorage(List<SCMOrgShortDto> orgs, long scmId) {
        int updatedCount = 0;
        int createdCount = 0;
        getScmOrThrow(scmId);
        for (SCMOrgShortDto org : orgs) {
            ScmOrg existingOrg = getOrg(scmId, org.getOrgIdentity());
            if (existingOrg != null) {
                updateTokenId(existingOrg, org.getTokenId());
                updatedCount++;
            } else {
                createOrg(org, scmId);
                createdCount++;
            }
        }

        log.trace("importOrgsIntoStorage: orgs created: {}, updated: {}.", createdCount, updatedCount);
    }

    private void updateTokenId(ScmOrg org, long newTokenId) {
        Token token = getTokenOrThrow(newTokenId);
        org.setAccessToken(token);
        scmOrgRepository.saveAndFlush(org);
    }

    public void createOrg(SCMOrgShortDto org, long scmId) {
        Token token = getTokenOrThrow(org.getTokenId());
        Scm scm = getScmOrThrow(scmId);
        ScmOrg orgForStorage = ScmOrg.builder()
                .accessToken(token)
                .scm(scm)
                .orgIdentity(org.getOrgIdentity())
                .build();
        scmOrgRepository.saveAndFlush(orgForStorage);
    }

    private SCMOrgDto toWebDto(ScmOrg org) {
        SCMOrgDto result = modelMapper.map(org, SCMOrgDto.class);
        result.setScmId(org.getScm().getId());

        Long tokenId = Optional.ofNullable(org.getAccessToken())
                .map(Token::getId)
                .orElse(0L);
        result.setTokenId(tokenId);
        return result;
    }

    private ScmOrg createOrg(long scmId, String orgIdentity) {
        log.trace("createOrgIfDoesntExist: org not found, creating a new one.");

        Scm scm = getScmOrThrow(scmId);

        ScmOrg scmOrg = ScmOrg.builder()
                .orgIdentity(orgIdentity)
                .scm(scm)
                .build();

        return scmOrgRepository.saveAndFlush(scmOrg);
    }

    private Scm getScmOrThrow(long scmId) {
        return scmRepository.findById(scmId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessages.INVALID_SCM_ID));
    }

    private Token getTokenOrThrow(long tokenId) {
        return tokenRepository.findById(tokenId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessages.INVALID_TOKEN_ID));
    }

    public SCMOrgDto getOrgOrThrow(long scmId, String orgIdentity) {
        return Optional.ofNullable(getOrg(scmId, orgIdentity))
                .map(this::toWebDto)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(ErrorMessages.ORG_NOT_FOUND_BY_IDENTITY, orgIdentity, scmId)));
    }
}
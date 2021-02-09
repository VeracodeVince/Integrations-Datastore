package com.checkmarx.integrations.datastore.services;

import com.checkmarx.integrations.datastore.controllers.exceptions.EntityNotFoundException;
import com.checkmarx.integrations.datastore.dto.SCMOrgDto;
import com.checkmarx.integrations.datastore.dto.SCMOrgShortDto;
import com.checkmarx.integrations.datastore.dto.SCMOrgUpdateDto;
import com.checkmarx.integrations.datastore.models.Scm;
import com.checkmarx.integrations.datastore.models.ScmOrg;
import com.checkmarx.integrations.datastore.models.Token;
import com.checkmarx.integrations.datastore.repositories.ScmOrgRepository;
import com.checkmarx.integrations.datastore.utils.ErrorMessages;
import com.checkmarx.integrations.datastore.utils.ObjectMapperUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrgService {
    @PersistenceContext
    private EntityManager entityManager;

    private final ScmOrgRepository scmOrgRepository;

    public ScmOrg getOrg(long scmId, String orgIdentity) {
        return scmOrgRepository.getScmOrg(scmId, orgIdentity);
    }

    public SCMOrgDto getOrgOrThrow(long scmId, String orgIdentity) {
        return Optional.ofNullable(getOrg(scmId, orgIdentity))
                .map(OrgService::toWebDto)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(ErrorMessages.ORG_NOT_FOUND_BY_IDENTITY, orgIdentity, scmId)));
    }

    public SCMOrgDto getOrgByRepoBaseUrl(String orgIdentity, String repoBaseUrl) {
        return Optional.ofNullable(scmOrgRepository.getByRepoBaseUrl(orgIdentity, repoBaseUrl))
                .map(OrgService::toWebDto)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(ErrorMessages.ORG_NOT_FOUND_BY_REPO, orgIdentity, repoBaseUrl)));
    }

    public void deleteScmOrgById(long id) {
        scmOrgRepository.deleteById(id);
    }

    public void updateOrg(ScmOrg org, SCMOrgUpdateDto updateRequest) {
        log.trace("updateOrg: org ID: {}, update request: {}", org.getId(), updateRequest);

        Optional.ofNullable(updateRequest.getCxGoToken())
                .ifPresent(org::setCxGoToken);

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
            log.trace("createOrgIfDoesntExist: org not found, creating a new one.");
            Scm scmRef = entityManager.getReference(Scm.class, scmId);
            ScmOrg scmOrg = ScmOrg.builder()
                    .orgIdentity(orgIdentity)
                    .scm(scmRef)
                    .build();

            return scmOrgRepository.saveAndFlush(scmOrg);
        }
    }

    public void importOrgsIntoStorage(List<SCMOrgShortDto> orgs, long scmId) {
        int updatedCount = 0;
        int createdCount = 0;
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

    public void updateTokenId(ScmOrg org, long newTokenId) {
        Token tokenRef = entityManager.getReference(Token.class, newTokenId);
        org.setAccessToken(tokenRef);
        scmOrgRepository.saveAndFlush(org);
    }

    public void createOrg(SCMOrgShortDto org, long scmId) {
        Token tokenRef = entityManager.getReference(Token.class, org.getTokenId());
        Scm scmRef = entityManager.getReference(Scm.class, scmId);
        ScmOrg orgForStorage = ScmOrg.builder()
                .accessToken(tokenRef)
                .scm(scmRef)
                .orgIdentity(org.getOrgIdentity())
                .build();
        scmOrgRepository.saveAndFlush(orgForStorage);
    }

    private static SCMOrgDto toWebDto(ScmOrg org) {
        SCMOrgDto result = ObjectMapperUtil.map(org, SCMOrgDto.class);
        result.setScmId(org.getScm().getId());
        result.setTokenId(org.getAccessToken().getId());
        return result;
    }
}
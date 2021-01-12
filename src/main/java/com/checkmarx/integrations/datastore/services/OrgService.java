package com.checkmarx.integrations.datastore.services;

import com.checkmarx.integrations.datastore.dto.CxFlowPropertiesDto;
import com.checkmarx.integrations.datastore.dto.SCMOrgDto;
import com.checkmarx.integrations.datastore.models.Scm;
import com.checkmarx.integrations.datastore.models.ScmOrg;
import com.checkmarx.integrations.datastore.models.Token;
import com.checkmarx.integrations.datastore.repositories.ScmOrgRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrgService {

    @PersistenceContext
    private EntityManager entityManager;

    private final ScmOrgRepository scmOrgRepository;

    public ScmOrg getOrgBy(String scmBaseUrl, String orgIdentity) {
        return scmOrgRepository.getScmOrg(orgIdentity, scmBaseUrl);
    }

    public void deleteScmOrgById(Long id) {
        scmOrgRepository.deleteById(id);
    }

    public void updateCxFlowProperties(ScmOrg scmOrg, CxFlowPropertiesDto cxFlowPropertiesDto) {
        createOrUpdateCxFlowUrl(scmOrg, cxFlowPropertiesDto.getCxFlowUrl());
        createOrUpdateCxGoToken(scmOrg, cxFlowPropertiesDto.getCxGoToken());
        createOrUpdateCxTeam(scmOrg, cxFlowPropertiesDto.getCxTeam());
    }

    public ScmOrg createOrGetScmOrgByOrgIdentity(Scm scm, String orgIdentity) {
        ScmOrg orgByIdentity = getOrgBy(scm.getBaseUrl(), orgIdentity);

        if (orgByIdentity != null) {
            log.trace("createOrGetScmOrgByOrgIdentity: orgByIdentity exists:{}", orgByIdentity);
            return orgByIdentity;
        } else {
            log.trace("createOrGetScmOrgByOrgIdentity: creating new orgByIdentity");
            ScmOrg scmOrg = ScmOrg.builder()
                    .orgIdentity(orgIdentity)
                    .scm(scm)
                    .build();
            return createScmOrg(scmOrg);
        }
    }

    private ScmOrg createScmOrg(ScmOrg scmOrg) {
        return scmOrgRepository.saveAndFlush(scmOrg);
    }

    private void createOrUpdateCxTeam(ScmOrg scmOrg, String cxTeam) {
        Optional.ofNullable(cxTeam).ifPresent(team -> {
            if (scmOrg.getTeam() != null) {
                scmOrg.setTeam(team);
                scmOrgRepository.save(scmOrg);
            } else {
                scmOrg.setTeam(team);
                scmOrgRepository.saveAndFlush(scmOrg);
            }
        });
    }

    private void createOrUpdateCxGoToken(ScmOrg scmOrg, String cxGoToken) {
        Optional.ofNullable(cxGoToken).ifPresent(token -> {
            if (scmOrg.getCxGoToken() != null) {
                scmOrg.setCxGoToken(token);
                scmOrgRepository.save(scmOrg);
            } else {
                scmOrg.setCxGoToken(token);
                scmOrgRepository.saveAndFlush(scmOrg);
            }
        });
    }

    private void createOrUpdateCxFlowUrl(ScmOrg scmOrg, String cxFlowUrl) {
        Optional.ofNullable(cxFlowUrl).ifPresent(url -> {
            if (scmOrg.getCxFlowUrl() != null) {
                scmOrg.setCxFlowUrl(url);
                scmOrgRepository.save(scmOrg);
            } else {
                scmOrg.setCxFlowUrl(url);
                scmOrgRepository.saveAndFlush(scmOrg);
            }
        });
    }

    public void updateTokenId(ScmOrg org, long newTokenId) {
        Token tokenRef = entityManager.getReference(Token.class, newTokenId);
        org.setAccessToken(tokenRef);
        scmOrgRepository.saveAndFlush(org);
    }

    public void createOrg(SCMOrgDto org, Scm scm) {
        Token tokenRef = entityManager.getReference(Token.class, org.getTokenId());
        ScmOrg orgForStorage = ScmOrg.builder()
                .accessToken(tokenRef)
                .scm(scm)
                .orgIdentity(org.getOrgIdentity())
                .build();
        scmOrgRepository.saveAndFlush(orgForStorage);
    }
}
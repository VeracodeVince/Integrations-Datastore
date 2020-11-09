package com.checkmarx.integrations.datastore.services;

import com.checkmarx.integrations.datastore.dto.CxFlowPropertiesDto;
import com.checkmarx.integrations.datastore.models.Scm;
import com.checkmarx.integrations.datastore.models.ScmOrg;
import com.checkmarx.integrations.datastore.repositories.ScmOrgRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrgService {

    private final ScmOrgRepository scmOrgRepository;

    public ScmOrg getOrgBy(String scmBaseUrl, String orgName) {
        return scmOrgRepository.getScmOrg(orgName, scmBaseUrl);
    }

    public ScmOrg createScmOrg(ScmOrg scmOrg) {
        return scmOrgRepository.saveAndFlush(scmOrg);
    }

    public void deleteScmOrgById(Long id) {
        scmOrgRepository.deleteById(id);
    }

    public void updateCxFlowProperties(ScmOrg scmOrg, CxFlowPropertiesDto cxFlowPropertiesDto) {
        createOrUpdateCxFlowUrl(scmOrg, cxFlowPropertiesDto.getCxFlowUrl());
        createOrUpdateCxGoToken(scmOrg, cxFlowPropertiesDto.getCxGoToken());
        createOrUpdateCxTeam(scmOrg, cxFlowPropertiesDto.getCxTeam());
    }

    ScmOrg createOrGetScmOrgByName(Scm scm, String orgName) {
        ScmOrg orgByName = getOrgBy(scm.getBaseUrl(), orgName);

        if (orgByName != null) {
            return orgByName;
        } else {
            ScmOrg scmOrg = ScmOrg.builder()
                    .name(orgName)
                    .scm(scm)
                    .build();
            return createScmOrg(scmOrg);
        }
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
}
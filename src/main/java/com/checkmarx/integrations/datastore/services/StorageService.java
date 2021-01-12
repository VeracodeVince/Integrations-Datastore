package com.checkmarx.integrations.datastore.services;

import com.checkmarx.integrations.datastore.dto.SCMOrgDto;
import com.checkmarx.integrations.datastore.models.Scm;
import com.checkmarx.integrations.datastore.models.ScmOrg;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Used for actions that involve several entity services at the same time.
 * TODO: move relevant logic from controllers and other services here.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StorageService {
    private final ScmService scmService;
    private final OrgService orgService;

    public void mergeOrgsIntoStorage(List<SCMOrgDto> orgs, String scmBaseUrl) {
        int updatedCount = 0;
        int createdCount = 0;
        Scm scm = scmService.getScmByScmUrl(scmBaseUrl);
        for (SCMOrgDto org : orgs) {
            ScmOrg existingOrg = orgService.getOrgBy(scmBaseUrl, org.getOrgIdentity());
            if (existingOrg != null) {
                orgService.updateTokenId(existingOrg, org.getTokenId());
                updatedCount++;
            } else {
                orgService.createOrg(org, scm);
                createdCount++;
            }
        }
        log.trace("mergeOrgsIntoStorage: orgs created: {}, updated: {}.", createdCount, updatedCount);
    }
}

package com.checkmarx.integrations.datastore.services;

import com.checkmarx.integrations.datastore.controllers.exceptions.EntityNotFoundException;
import com.checkmarx.integrations.datastore.dto.AccessTokenDto;
import com.checkmarx.integrations.datastore.dto.ReposUpdateDto;
import com.checkmarx.integrations.datastore.dto.SCMCreateDto;
import com.checkmarx.integrations.datastore.dto.TenantDto;
import com.checkmarx.integrations.datastore.models.ScmOrg;
import com.checkmarx.integrations.datastore.models.ScmType;
import com.checkmarx.integrations.datastore.utils.ErrorMessages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Used for actions that involve several entity services at the same time.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StorageService {
    private final RepoService repoService;
    private final OrgService orgService;
    private final TokenService tokenService;
    private final TenantService tenantService;
    private final ScmService scmService;
    private final ScmTypeService scmTypeService;

    public void importOrgAndRepos(ReposUpdateDto updateRequest) {
        ScmOrg org = orgService.createOrgIfDoesntExist(updateRequest.getScmId(), updateRequest.getOrgIdentity());
        repoService.updateRepos(org, updateRequest.getRepoList());
    }

    public AccessTokenDto getScmAccessToken(long scmId, String orgIdentity) {
        ScmOrg org = orgService.getOrg(scmId, orgIdentity);

        return Optional.ofNullable(org)
                .map(ScmOrg::getAccessToken)
                .map(tokenService::toTokenResponse)
                .orElseThrow(tokenNotFoundException(scmId, orgIdentity));
    }

    public TenantDto getTenant(long scmId, String orgIdentity) {
        ScmOrg org = orgService.getOrg(scmId, orgIdentity);

        return Optional.ofNullable(org)
                .map(ScmOrg::getTenant)
                .map(tenantService::toTenantResponse)
                .orElseThrow(tenantNotFoundException(scmId, orgIdentity));
    }

    public long createScm(SCMCreateDto scm) {
        ScmType scmType = scmTypeService.getByName(scm.getType());
        return scmService.createScm(scm, scmType);
    }

    private static Supplier<EntityNotFoundException> tokenNotFoundException(long scmId, String orgIdentity) {
        return () -> {
            String message = String.format(ErrorMessages.ACCESS_TOKEN_NOT_FOUND, scmId, orgIdentity);
            return new EntityNotFoundException(message);
        };
    }

    private static Supplier<EntityNotFoundException> tenantNotFoundException(long scmId, String orgIdentity) {
        return () -> {
            String message = String.format(ErrorMessages.TENANT_NOT_FOUND, scmId, orgIdentity);
            return new EntityNotFoundException(message);
        };
    }
}

package com.checkmarx.integrations.datastore.services;

import com.checkmarx.integrations.datastore.controllers.exceptions.ScmOrgNotFoundException;
import com.checkmarx.integrations.datastore.dto.CxFlowPropertiesDto;
import com.checkmarx.integrations.datastore.dto.SCMOrgDto;
import com.checkmarx.integrations.datastore.models.Scm;
import com.checkmarx.integrations.datastore.models.ScmOrg;
import com.checkmarx.integrations.datastore.models.Token;
import com.checkmarx.integrations.datastore.repositories.ScmOrgRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.checkmarx.integrations.datastore.utils.ErrorConstsMessages.BASE_URL_WITH_ORG_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrgService {

    private final ScmOrgRepository scmOrgRepository;
    private final ScmService scmService;
    private final TokenService tokenService;

    public ScmOrg getOrgBy(String scmBaseUrl, String orgIdentity) {
        return scmOrgRepository.getScmOrg(orgIdentity, scmBaseUrl);
    }

    public SCMOrgDto getOrgByName(String scmBaseUrl, String orgName) {
        ScmOrg scmOrg = Optional.ofNullable(scmOrgRepository.getScmOrgByName(scmBaseUrl, orgName))
                .orElseThrow(() -> new ScmOrgNotFoundException(String.format(BASE_URL_WITH_ORG_NOT_FOUND, scmBaseUrl, orgName)));
        Token tokenByOrgIdentity = tokenService.getTokenByOrgIdentity(scmOrg.getOrgIdentity());

        SCMOrgDto scmOrgDto;
        if (Optional.ofNullable(tokenByOrgIdentity).isPresent()) {
            scmOrgDto = getFullScmOrgDto(scmOrg, tokenByOrgIdentity);
        } else {
            scmOrgDto = getScmOrgDtoWithoutToken(scmOrg);
        }
        return scmOrgDto;
    }

    public void deleteScmOrgById(Long id) {
        scmOrgRepository.deleteById(id);
    }

    public void updateCxFlowProperties(ScmOrg scmOrg, CxFlowPropertiesDto cxFlowPropertiesDto) {
        createOrUpdateCxFlowUrl(scmOrg, cxFlowPropertiesDto.getCxFlowUrl());
        createOrUpdateCxGoToken(scmOrg, cxFlowPropertiesDto.getCxGoToken());
        createOrUpdateCxTeam(scmOrg, cxFlowPropertiesDto.getCxTeam());
    }

    public void createScmOrgByScmOrgDto(SCMOrgDto scmOrgDto) {
        Scm scmByScmUrl = scmService.getScmByScmUrl(scmOrgDto.getScmUrl());
        ScmOrg scmOrg = createOrGetScmOrgByOrgIdentity(scmByScmUrl, scmOrgDto.getOrgIdentity());
        Optional.ofNullable(scmOrgDto.getOrgName()).ifPresent(scmOrg::setOrgName);
        Optional.ofNullable(scmOrgDto.getOrgIdentity()).ifPresent(scmOrg::setOrgIdentity);
        scmOrgRepository.save(scmOrg);

        tokenService.updateTokenIfExists(scmOrg, scmOrgDto.getTokenType(), scmOrgDto.getAccessToken());
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

    private SCMOrgDto getFullScmOrgDto(ScmOrg scmOrg, Token token) {
        return SCMOrgDto.builder()
                .scmUrl(scmOrg.getScm().getBaseUrl())
                .orgIdentity(scmOrg.getOrgIdentity())
                .orgName(scmOrg.getOrgName())
                .accessToken(token.getAccessToken())
                .tokenType(token.getType())
                .build();
    }

    private SCMOrgDto getScmOrgDtoWithoutToken(ScmOrg scmOrg) {
        return SCMOrgDto.builder()
                .scmUrl(scmOrg.getScm().getBaseUrl())
                .orgIdentity(scmOrg.getOrgIdentity())
                .orgName(scmOrg.getOrgName())
                .build();
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
}
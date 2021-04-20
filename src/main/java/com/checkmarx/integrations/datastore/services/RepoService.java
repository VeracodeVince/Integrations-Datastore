package com.checkmarx.integrations.datastore.services;

import com.checkmarx.integrations.datastore.controllers.exceptions.EntityNotFoundException;
import com.checkmarx.integrations.datastore.dto.RepoDto;
import com.checkmarx.integrations.datastore.dto.RepoUpdateDto;
import com.checkmarx.integrations.datastore.models.ScmOrg;
import com.checkmarx.integrations.datastore.models.ScmRepo;
import com.checkmarx.integrations.datastore.repositories.ScmOrgRepository;
import com.checkmarx.integrations.datastore.repositories.ScmRepoRepository;
import com.checkmarx.integrations.datastore.utils.ErrorMessages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RepoService {
    private final ScmRepoRepository repoRepository;
    private final ScmOrgRepository orgRepository;
    private final ModelMapper modelMapper;

    public List<RepoDto> getOrganizationRepos(long scmId, String orgIdentity) {
        ScmOrg org = orgRepository.getScmOrg(scmId, orgIdentity);
        return Optional.ofNullable(org)
                .map(toChildRepos())
                .orElseThrow(notFoundException(scmId, orgIdentity));
    }

    public RepoDto getScmRepo(long scmId, String orgIdentity, String repoIdentity) {
        ScmRepo repo = repoRepository.getRepo(scmId, orgIdentity, repoIdentity);
        return Optional.ofNullable(repo)
                .map(aRepo -> modelMapper.map(aRepo, RepoDto.class))
                .orElseThrow(() -> new EntityNotFoundException(String.format(ErrorMessages.REPO_NOT_FOUND, repoIdentity)));
    }

    public void updateRepos(ScmOrg scmOrg, List<RepoDto> repoDtoList) {
        repoDtoList.forEach(repoDto -> {
            String repoIdentity = repoDto.getRepoIdentity();

            if (repoExists(scmOrg, repoIdentity)) {
                ScmRepo repoToUpdate = repoRepository.getRepoByIdentity(scmOrg.getOrgIdentity(), repoIdentity);
                copyPropertyValues(repoDto, repoToUpdate);
                repoRepository.save(repoToUpdate);
            } else {
                ScmRepo scmRepo = ScmRepo.builder()
                        .scmOrg(scmOrg)
                        .repoIdentity(repoIdentity)
                        .webhookId(repoDto.getWebhookId())
                        .isWebhookConfigured(repoDto.isWebhookConfigured())
                        .build();
                repoRepository.saveAndFlush(scmRepo);
            }
        });
    }

    public void updateRepo(long scmId, String orgIdentity, String repoIdentity, RepoUpdateDto repo) {
        log.trace("Looking for the repo in storage.");
        ScmRepo repoToUpdate = repoRepository.findRepo(scmId, orgIdentity, repoIdentity);
        if (repoToUpdate == null) {
            throw new EntityNotFoundException(String.format(ErrorMessages.REPO_NOT_FOUND, repoIdentity));
        }
        log.trace("Repo found, ID: {}.", repoToUpdate.getId());

        copyPropertyValues(repo, repoToUpdate);

        repoRepository.saveAndFlush(repoToUpdate);
        log.trace("Repo updated successfully.");
    }

    private Supplier<EntityNotFoundException> notFoundException(long scmId, String orgIdentity) {
        return () -> {
            String message = String.format(ErrorMessages.ORG_NOT_FOUND_BY_IDENTITY, orgIdentity, scmId);
            return new EntityNotFoundException(message);
        };
    }

    private Function<ScmOrg, List<RepoDto>> toChildRepos() {
        return org -> org.getRepos().stream()
                .map(element -> modelMapper.map(element, RepoDto.class))
                .collect(Collectors.toList());
    }

    private boolean repoExists(ScmOrg scmOrg, String repoIdentity) {
        return repoRepository.getRepoByIdentity(scmOrg.getOrgIdentity(), repoIdentity) != null;
    }

    private static void copyPropertyValues(RepoUpdateDto src, ScmRepo target) {
        target.setWebhookId(src.getWebhookId());
        target.setWebhookConfigured(src.isWebhookConfigured());
        target.setWebhookKey(src.getWebhookKey());
    }
}
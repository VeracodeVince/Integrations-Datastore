package com.checkmarx.integrations.datastore.services;

import com.checkmarx.integrations.datastore.controllers.exceptions.EntityNotFoundException;
import com.checkmarx.integrations.datastore.dto.RepoDto;
import com.checkmarx.integrations.datastore.dto.RepoUpdateDto;
import com.checkmarx.integrations.datastore.models.ScmOrg;
import com.checkmarx.integrations.datastore.models.ScmRepo;
import com.checkmarx.integrations.datastore.repositories.ScmRepoRepository;
import com.checkmarx.integrations.datastore.utils.ErrorMessages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RepoService {
    private final ScmRepoRepository scmRepoRepository;
    private final ModelMapper modelMapper;

    public List<RepoDto> getScmReposByOrgIdentity(long scmId, String orgIdentity) {
        return scmRepoRepository.getScmReposByOrgIdentity(scmId, orgIdentity).stream()
                .map(element -> modelMapper.map(element, RepoDto.class))
                .collect(Collectors.toList());
    }

    public RepoDto getScmRepo(long scmId, String orgIdentity, String repoIdentity) {
        ScmRepo repo = scmRepoRepository.getRepo(scmId, orgIdentity, repoIdentity);
        return Optional.ofNullable(repo)
                .map(aRepo -> modelMapper.map(aRepo, RepoDto.class))
                .orElseThrow(() -> new EntityNotFoundException(String.format(ErrorMessages.REPO_NOT_FOUND, repoIdentity)));
    }

    public void updateRepos(ScmOrg scmOrg, List<RepoDto> repoDtoList) {
        repoDtoList.forEach(repoDto -> {
            String repoIdentity = repoDto.getRepoIdentity();

            if (isScmRepoExists(scmOrg, repoIdentity)) {
                ScmRepo repoToUpdate = scmRepoRepository.getRepoByIdentity(scmOrg.getOrgIdentity(), repoIdentity);
                copyPropertyValues(repoDto, repoToUpdate);
                scmRepoRepository.save(repoToUpdate);
            } else {
                ScmRepo scmRepo = ScmRepo.builder()
                        .scmOrg(scmOrg)
                        .repoIdentity(repoIdentity)
                        .webhookId(repoDto.getWebhookId())
                        .isWebhookConfigured(repoDto.isWebhookConfigured())
                        .build();
                scmRepoRepository.saveAndFlush(scmRepo);
            }
        });
    }

    public void updateRepo(long scmId, String orgIdentity, String repoIdentity, RepoUpdateDto repo) {
        log.trace("Looking for the repo in storage.");
        ScmRepo repoToUpdate = scmRepoRepository.findRepo(scmId, orgIdentity, repoIdentity);
        if (repoToUpdate == null){
            throw new EntityNotFoundException(String.format(ErrorMessages.REPO_NOT_FOUND, repoIdentity));
        }
        log.trace("Repo found, ID: {}.", repoToUpdate.getId());

        copyPropertyValues(repo, repoToUpdate);

        scmRepoRepository.saveAndFlush(repoToUpdate);
        log.trace("Repo updated successfully.");
    }

    private boolean isScmRepoExists(ScmOrg scmOrg, String repoIdentity) {
        return scmRepoRepository.getRepoByIdentity(scmOrg.getOrgIdentity(), repoIdentity) != null;
    }

    private static void copyPropertyValues(RepoUpdateDto src, ScmRepo target) {
        target.setWebhookId(src.getWebhookId());
        target.setWebhookConfigured(src.isWebhookConfigured());
        target.setWebhookKey(src.getWebhookKey());
    }
}
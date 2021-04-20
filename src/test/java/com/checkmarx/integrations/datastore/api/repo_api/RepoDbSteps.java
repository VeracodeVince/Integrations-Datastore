package com.checkmarx.integrations.datastore.api.repo_api;

import com.checkmarx.integrations.datastore.api.shared.RepoInitializer;
import com.checkmarx.integrations.datastore.api.shared.SharedSteps;
import com.checkmarx.integrations.datastore.models.Scm;
import com.checkmarx.integrations.datastore.models.ScmOrg;
import com.checkmarx.integrations.datastore.models.ScmRepo;
import com.checkmarx.integrations.datastore.repositories.ScmOrgRepository;
import com.checkmarx.integrations.datastore.repositories.ScmRepoRepository;
import com.checkmarx.integrations.datastore.repositories.ScmRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RequiredArgsConstructor
public class RepoDbSteps {
    private final ScmRepoRepository scmRepoRepository;
    private final ScmOrgRepository orgRepo;
    private final ScmRepository scmRepo;

    @Given("database contains an organization with id: {int}, scmId: {int}, orgIdentity: {word}")
    public void dbContainsOrg(long orgId, long scmId, String orgIdentity) {
        Scm scm = scmRepo.findById(scmId).orElse(null);
        ScmOrg org = ScmOrg.builder()
                .scm(scm)
                .orgIdentity(orgIdentity).build();

        ScmOrg createdOrg = orgRepo.saveAndFlush(org);
        assertNotNull("Created org ID is null.", createdOrg.getId());
        assertEquals("Unexpected ID of a created org.", orgId, createdOrg.getId().longValue());
    }

    @Given("^database contains an organization with scmId: (\\d+), orgIdentity: (\\S+)(?:, with no related repos)?$")
    public void givenOrgWithoutRepos(long scmId, String orgIdentity) {
        dbContainsOrg(1, scmId, orgIdentity);
    }

    @Given("database contains the following repo(s):")
    public void givenRepos(List<Map<String, String>> repos) {
        scmRepoRepository.saveAll(repos.stream()
                .map(toRepo())
                .collect(Collectors.toList()));
    }

    @Given("database contains the following organizations:")
    public void givenOrgs(List<Map<String, String>> orgs) {
        List<ScmOrg> orgsToSave = orgs.stream()
                .map(toScmOrg())
                .collect(Collectors.toList());

        orgRepo.saveAll(orgsToSave);
        orgRepo.flush();
    }

    @Then("database contains a repo with id: {int}, repoIdentity: {word}, webhookId: {word}, isWebhookConfigured: {word}, orgId: {word}")
    public void thenDbContainsRepo(long repoId, String repoIdentity, String webhookId, String isWebhookConfigured, String orgId) {
        ScmRepo repo = scmRepoRepository.findById(repoId).orElse(null);
        assertNotNull(String.format("Repo with ID %d doesn't exist in DB.", repoId), repo);

        SharedSteps.validateFieldValue("repoIdentity", repoIdentity, repo.getRepoIdentity());
        SharedSteps.validateFieldValue("webhookId", webhookId, repo.getWebhookId());

        SharedSteps.validateFieldValue("isWebhookConfigured",
                isWebhookConfigured,
                Boolean.toString(repo.isWebhookConfigured()));

        ScmOrg parentOrg = repo.getScmOrg();
        assertNotNull("Unable to get repo organization.", parentOrg);
        SharedSteps.validateFieldValue("orgId", orgId, parentOrg.getId().toString());
    }

    @Then("database contains, among others, the following repos:")
    public void thenDbContainsRepos(List<Map<String, String>> repos) {
        for (Map<String, String> repoMap : repos) {
            thenDbContainsRepo(
                    Long.parseLong(repoMap.get("id")),
                    repoMap.get("repoIdentity"),
                    repoMap.get("webhookId"),
                    repoMap.get("isWebhookConfigured"),
                    repoMap.get("orgId")
            );
        }
    }

    @Then("database now contains an organization with id: {int}, scmId: {int}, orgIdentity: {word}")
    public void thenDbContainsOrg(long orgId, long scmId, String orgIdentity) {
        ScmOrg org = orgRepo.findById(orgId).orElse(null);
        assertNotNull(String.format("Unable to get organization with id: %d.", orgId), org);

        Scm parentScm = org.getScm();
        assertNotNull("Parent SCM is null.", parentScm);
        assertEquals("Unexpected scmId.", scmId, parentScm.getId());

        assertEquals("Unexpected orgIdentity.", orgIdentity, org.getOrgIdentity());
    }

    private Function<Map<String, String>, ScmRepo> toRepo() {
        return repoMap -> {
            long orgId = Long.parseLong(repoMap.get("orgId"));
            ScmOrg parentOrg = orgRepo.findById(orgId).orElse(null);
            return ScmRepo.builder()
                    .repoIdentity(repoMap.get("repoIdentity"))
                    .webhookId(repoMap.get("webhookId"))
                    .isWebhookConfigured(Boolean.parseBoolean(repoMap.get("isWebhookConfigured")))
                    .scmOrg(parentOrg)
                    .build();
        };
    }

    private Function<Map<String, String>, ScmOrg> toScmOrg() {
        return org -> {
            Scm scm = scmRepo.getOne(Long.parseLong(org.get("scmId")));
            return ScmOrg.builder()
                    .orgIdentity(org.get("orgIdentity"))
                    .scm(scm)
                    .build();
        };
    }
}

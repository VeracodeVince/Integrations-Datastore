package com.checkmarx.integrations.datastore.repositories;

import com.checkmarx.integrations.datastore.models.ScmRepo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ScmRepoRepository extends JpaRepository<ScmRepo, Long> {

    @Query(value = "SELECT r FROM  ScmRepo r WHERE r.scmOrg.scm.baseUrl = ?1 AND r.scmOrg.orgIdentity = ?2")
    List<ScmRepo> getScmReposByOrgIdentity(String scmBaseUrl, String orgIdentity);

    @Query(value = "SELECT r FROM  ScmRepo r WHERE r.scmOrg.scm.baseUrl = ?1 AND r.scmOrg.orgIdentity = ?2 AND r.repoIdentity = ?3")
    ScmRepo getRepo(String scmBaseUrl, String orgIdentity, String repoIdentity);

    @Query(value = "SELECT r FROM ScmRepo r WHERE r.scmOrg.orgIdentity = ?1 AND r.repoIdentity = ?2")
    ScmRepo getRepoByIdentity(String orgIdentity, String repoIdentity);
}
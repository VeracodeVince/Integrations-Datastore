package com.checkmarx.integrations.datastore.repositories;

import com.checkmarx.integrations.datastore.models.ScmRepo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ScmRepoRepository extends JpaRepository<ScmRepo, Long> {

    @Query(value = "SELECT r FROM ScmRepo r WHERE r.scmOrg.scm.id = ?1 AND r.scmOrg.orgIdentity = ?2 AND r.repoIdentity = ?3")
    ScmRepo getRepo(long scmId, String orgIdentity, String repoIdentity);

    @Query(value = "SELECT r FROM ScmRepo r WHERE r.scmOrg.orgIdentity = ?1 AND r.repoIdentity = ?2")
    ScmRepo getRepoByIdentity(String orgIdentity, String repoIdentity);

    @Query(value = "SELECT r FROM ScmRepo r WHERE r.scmOrg.scm.id = ?1 AND r.scmOrg.orgIdentity = ?2 AND r.repoIdentity = ?3")
    ScmRepo findRepo(long scmId, String orgIdentity, String repoIdentity);
}
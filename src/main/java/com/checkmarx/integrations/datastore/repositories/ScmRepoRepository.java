package com.checkmarx.integrations.datastore.repositories;

import com.checkmarx.integrations.datastore.models.ScmRepo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ScmRepoRepository extends JpaRepository<ScmRepo, Long> {

    @Query(value = "SELECT r FROM ScmRepo r WHERE r.name = ?1 AND r.scmOrg.name = ?2 AND r.scmOrg.scm.name = ?3")
    ScmRepo getRepo(String repo, String nameSpace, String scmName);
}
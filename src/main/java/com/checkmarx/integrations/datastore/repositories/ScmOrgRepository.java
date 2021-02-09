package com.checkmarx.integrations.datastore.repositories;

import com.checkmarx.integrations.datastore.models.ScmOrg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ScmOrgRepository extends JpaRepository<ScmOrg, Long> {

    @Query(value = "SELECT s FROM ScmOrg s WHERE s.scm.id = ?1 AND s.orgIdentity = ?2")
    ScmOrg getScmOrg(long scmId, String orgIdentity);

    @Query(value = "SELECT org FROM ScmOrg org where org.orgIdentity = ?1 and org.scm.repoBaseUrl = ?2")
    ScmOrg getByRepoBaseUrl(String orgIdentity, String repoBaseUrl);
}
package com.checkmarx.integrations.datastore.repositories;

import com.checkmarx.integrations.datastore.models.ScmOrg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ScmOrgRepository extends JpaRepository<ScmOrg, Long> {

    @Query(value = "SELECT s FROM ScmOrg s WHERE s.orgIdentity = ?1 AND s.scm.baseUrl = ?2")
    ScmOrg getScmOrg(String orgIdentity, String scmBaseUrl);

    @Query(value = "SELECT s FROM ScmOrg s WHERE s.scm.baseUrl = ?1 AND s.orgName = ?2")
    ScmOrg getScmOrgByName(String scmBaseUrl, String orgName);
}
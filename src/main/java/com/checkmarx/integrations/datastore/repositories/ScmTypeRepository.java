package com.checkmarx.integrations.datastore.repositories;

import com.checkmarx.integrations.datastore.models.ScmType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ScmTypeRepository extends JpaRepository<ScmType, Long> {
    @Query(value = "SELECT t FROM ScmType t WHERE t.name = ?1")
    ScmType getByName(String name);
}

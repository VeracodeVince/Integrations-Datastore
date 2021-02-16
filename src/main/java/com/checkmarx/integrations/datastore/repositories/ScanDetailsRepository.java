package com.checkmarx.integrations.datastore.repositories;

import com.checkmarx.integrations.datastore.models.ScanDetails;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface ScanDetailsRepository extends Repository<ScanDetails, Long> {
    Optional<ScanDetails> findByScanId(String scanId);

    ScanDetails save(ScanDetails scanDetails);
}

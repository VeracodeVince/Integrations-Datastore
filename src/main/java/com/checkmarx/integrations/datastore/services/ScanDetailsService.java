package com.checkmarx.integrations.datastore.services;

import com.checkmarx.integrations.datastore.controllers.exceptions.DataStoreException;
import com.checkmarx.integrations.datastore.controllers.exceptions.DuplicateKeyException;
import com.checkmarx.integrations.datastore.controllers.exceptions.EntityNotFoundException;
import com.checkmarx.integrations.datastore.dto.CreateScanDetailsDto;
import com.checkmarx.integrations.datastore.models.ScanDetails;
import com.checkmarx.integrations.datastore.repositories.ScanDetailsRepository;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScanDetailsService {
    private final ScanDetailsRepository repo;
    private final ModelMapper modelMapper;

    public JsonNode getScanDetailsByScanId(String scanId) {
        return repo.findByScanId(scanId)
                .map(ScanDetails::getBody)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Unable to find details for scan: %s", scanId)));
    }

    public void createScanDetails(CreateScanDetailsDto detailsDto) {
        if (detailsDto.getBody().isNull()) {
            throw new DataStoreException("Scan details body must be provided.");
        }

        ScanDetails details = modelMapper.map(detailsDto, ScanDetails.class);
        try {
            repo.save(details);
        } catch (DataIntegrityViolationException e) {
            String message = String.format("Unable to create details for scan %s. The details already exist.",
                    Optional.ofNullable(details).map(ScanDetails::getScanId).orElse("n/a"));
            throw new DuplicateKeyException(message);
        }
    }
}

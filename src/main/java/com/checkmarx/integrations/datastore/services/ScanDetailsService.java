package com.checkmarx.integrations.datastore.services;

import com.checkmarx.integrations.datastore.controllers.exceptions.DuplicateKeyException;
import com.checkmarx.integrations.datastore.controllers.exceptions.EntityNotFoundException;
import com.checkmarx.integrations.datastore.dto.ScanDetailsDto;
import com.checkmarx.integrations.datastore.models.ScanDetails;
import com.checkmarx.integrations.datastore.repositories.ScanDetailsRepository;
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

    public ScanDetailsDto getScanDetailsByScanId(String scanId) {
        return repo.findByScanId(scanId)
                .map(details -> modelMapper.map(details, ScanDetailsDto.class))
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Unable to find details for scan: %s", scanId)));
    }

    public void createScanDetails(ScanDetailsDto detailsDto) {
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

package com.checkmarx.integrations.datastore.controllers;

import com.checkmarx.integrations.datastore.dto.ScanDetailsDto;
import com.checkmarx.integrations.datastore.services.ScanDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/scanDetails")
public class ScanDetailsController {
    private final ScanDetailsService scanDetailsService;

    @GetMapping(value = "{scanId}")
    public ScanDetailsDto getByScanId(@PathVariable String scanId) {
        return scanDetailsService.getScanDetailsByScanId(scanId);
    }

    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody ScanDetailsDto details) {
        scanDetailsService.createScanDetails(details);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}

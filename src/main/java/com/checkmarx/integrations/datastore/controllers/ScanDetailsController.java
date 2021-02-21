package com.checkmarx.integrations.datastore.controllers;

import com.checkmarx.integrations.datastore.dto.CreateScanDetailsDto;
import com.checkmarx.integrations.datastore.services.ScanDetailsService;
import com.fasterxml.jackson.databind.JsonNode;
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
    public JsonNode getByScanId(@PathVariable String scanId) {
        return scanDetailsService.getScanDetailsByScanId(scanId);
    }

    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody CreateScanDetailsDto details) {
        scanDetailsService.createScanDetails(details);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}

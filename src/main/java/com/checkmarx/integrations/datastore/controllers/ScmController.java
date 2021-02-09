package com.checkmarx.integrations.datastore.controllers;

import com.checkmarx.integrations.datastore.dto.SCMDto;
import com.checkmarx.integrations.datastore.dto.SCMCreateDto;
import com.checkmarx.integrations.datastore.services.ScmService;
import com.checkmarx.integrations.datastore.services.StorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/scms")
@Slf4j
public class ScmController {
    private final ScmService scmService;
    private final StorageService storageService;

    @Operation(summary = "Gets details of all SCMs")
    @GetMapping
    public List<SCMDto> getAllScms() {
        log.trace("getAllScms");
        List<SCMDto> result = scmService.getAllScms();
        log.trace("getAllScms: SCMs returned: {}.", result.size());
        return result;
    }

    @Operation(summary = "Gets an SCM")
    @GetMapping(value = "{scmId}")
    @ApiResponse(responseCode = "200", description = "SCM found.", content = @Content)
    @ApiResponse(responseCode = "404", description = "SCM not found.", content = @Content)
    public SCMDto getScmById(@PathVariable long scmId) {
        log.trace("getScmById: id:{}", scmId);
        return scmService.getScmById(scmId);
    }

    @Operation(summary = "Creates an SCM")
    @PostMapping
    @ApiResponse(responseCode = "201", description = "SCM was created successfully.", content = @Content)
    @ResponseStatus(code = HttpStatus.CREATED)
    public long createScm(@RequestBody SCMCreateDto scm) {
        log.trace("createScm: scm={}", scm);
        long id = storageService.createScm(scm);
        log.trace("createScm: new SCM id: {}", id);
        return id;
    }

    @Operation(summary = "Deletes an SCM")
    @DeleteMapping(value = "{scmId}")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void deleteScm(@PathVariable long scmId) {
        log.trace("deleteScm: id={}", scmId);
        scmService.deleteScm(scmId);
    }
}
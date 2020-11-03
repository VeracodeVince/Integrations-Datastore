package com.checkmarx.integrations.datastore.controllers;

import com.checkmarx.integrations.datastore.models.Scm;
import com.checkmarx.integrations.datastore.services.ScmService;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "Gets details of all SCMs")
    @GetMapping
    public List<Scm> getAllScms() {
        return scmService.getAllScms();
    }

    @Operation(summary = "Creates a new SCM")
    @PostMapping
    public Scm createScm(@RequestBody final Scm scm) {
        log.trace("createScm: scm={}", scm);
        return scmService.createScm(scm);
    }

    @Operation(summary = "Deletes a SCM by id")
    @DeleteMapping(value = "{id}")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void deleteScm(@PathVariable Long id) {
        log.trace("deleteScm: id={}", id);
        scmService.deleteScm(id);
    }

}
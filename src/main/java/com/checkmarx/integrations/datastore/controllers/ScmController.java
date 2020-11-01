package com.checkmarx.integrations.datastore.controllers;

import com.checkmarx.integrations.datastore.models.Scm;
import com.checkmarx.integrations.datastore.services.ScmService;
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

    @GetMapping
    public List<Scm> getAllScms() {
        return scmService.getAllScms();
    }

    @PostMapping
    public Scm createScm(@RequestBody final Scm scm) {
        log.trace("createScm: scm={}", scm);
        return scmService.createScm(scm);
    }

    @DeleteMapping
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void deleteScm(@RequestBody final Scm scm) {
        log.trace("deleteScm: scm={}", scm);
        scmService.deleteScm(scm);
    }

}
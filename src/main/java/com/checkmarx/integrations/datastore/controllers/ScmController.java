package com.checkmarx.integrations.datastore.controllers;

import com.checkmarx.integrations.datastore.models.Scm;
import com.checkmarx.integrations.datastore.services.ScmService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/scms")
public class ScmController {

    private final ScmService scmService;

    @PostMapping
    public Scm createScm(@RequestBody final Scm scm) {
        return scmService.createScm(scm);
    }

}
package com.checkmarx.integrations.datastore.api;

import com.checkmarx.integrations.datastore.IntegrationsDataStoreApplication;
import com.checkmarx.integrations.datastore.dto.SCMDto;
import com.checkmarx.integrations.datastore.models.Scm;
import com.checkmarx.integrations.datastore.repositories.ScmRepository;
import io.cucumber.java.en.*;
import io.cucumber.spring.CucumberContextConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Slf4j
@CucumberContextConfiguration
@SpringBootTest(classes = {IntegrationsDataStoreApplication.class})
public class ScmApiSteps {

    private String urlPatternDataSourceGetScm = "http://localhost:8083/scms/%s";

    private final ScmRepository scmRepository;

    private ResponseEntity<SCMDto> responseEntity;

    @Autowired
    RestTemplate restTemplate;

    public ScmApiSteps(
            ScmRepository scmRepository) {
        this.scmRepository = scmRepository;
    }

    @Given("data base contains {string}, {string} and {string}")
    public void initDataBaseValues(String baseUrl, String clientId, String clientSecret) {
        scmRepository.saveAndFlush(Scm.builder()
                                       .baseUrl(baseUrl)
                                       .clientId(clientId)
                                       .clientSecret(clientSecret)
                                       .build());
    }

    @When("API client calls the `get scm` API for {string}")
    public void apiClientCallsTheGetScmAPIFor(String scmBaseUrl) {
        String path = String.format(urlPatternDataSourceGetScm, scmBaseUrl);
        responseEntity = restTemplate.getForEntity(path, SCMDto.class);
    }

    @Then("response status is {int}")
    public void responseStatusIs(int HttpStatusCode) {
    }

    @And("response contains the {string} field set to {string}")
    public void responseContainsTheFieldSetTo(String baseUrl, String expectedBaseUrl) {
    }

    @And("response does not have other fields")
    public void responseDoesNotHaveOtherFields() {
    }
}

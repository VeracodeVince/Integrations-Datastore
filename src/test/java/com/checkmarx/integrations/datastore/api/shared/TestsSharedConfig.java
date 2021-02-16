package com.checkmarx.integrations.datastore.api.shared;


import com.checkmarx.integrations.datastore.DataStoreApp;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

public class TestsSharedConfig {

    private ConfigurableApplicationContext context;

    @Before
    public void before() {
        SpringApplication springApplication = new SpringApplication(DataStoreApp.class);
        context = springApplication.run();
    }

    @After
    public void after() {
        context.close();
    }
}
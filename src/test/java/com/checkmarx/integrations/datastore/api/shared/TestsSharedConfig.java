package com.checkmarx.integrations.datastore.api.shared;


import com.checkmarx.integrations.datastore.IntegrationsDataStoreApplication;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Collections;
import java.util.Random;

public class TestsSharedConfig {

    private ConfigurableApplicationContext context;

    @Before
    public void before() {
        SpringApplication springApplication=new SpringApplication(IntegrationsDataStoreApplication.class);
        Random random = new Random();
        int maxPort = 65535;
        int randomPort = random.nextInt(maxPort);
        springApplication.setDefaultProperties(Collections.singletonMap("server.port", randomPort));
        context =  springApplication.run();
    }

    @After
    public void after(){
        context.close();
    }
}
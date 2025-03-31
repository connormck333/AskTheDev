package com.devconnor.askthedev;

import com.devconnor.askthedev.utils.EnvUtils;
import com.devconnor.askthedev.utils.EnvironmentType;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AskTheDevApplication {

    private static final String ENVIRONMENT_KEY = "ENVIRONMENT";

    public static void main(String[] args) {
        EnvironmentType envType = System.getenv(ENVIRONMENT_KEY) != null ? EnvironmentType.REMOTE : EnvironmentType.LOCAL;
        EnvUtils.loadDotEnv(envType);
        SpringApplication.run(AskTheDevApplication.class, args);
    }

}

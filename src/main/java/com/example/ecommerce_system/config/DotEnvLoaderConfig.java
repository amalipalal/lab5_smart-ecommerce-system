package com.example.ecommerce_system.config;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class DotEnvLoaderConfig {

    private final ConfigurableEnvironment environment;

    public DotEnvLoaderConfig(ConfigurableEnvironment environment) {
        this.environment = environment;
    }

    @PostConstruct
    public void loadEnvFile() {
        // Load .env file from project root
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();

        Map<String, Object> props = new HashMap<>();
        dotenv.entries().forEach(entry -> props.put(entry.getKey(), entry.getValue()));

        // Insert into Spring Environment so application.properties can resolve ${VAR}
        environment.getPropertySources().addFirst(new MapPropertySource("dotenv", props));
    }
}

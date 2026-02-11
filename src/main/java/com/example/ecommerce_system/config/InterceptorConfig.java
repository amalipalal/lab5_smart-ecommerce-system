package com.example.ecommerce_system.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@AllArgsConstructor
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    private static final String API_BASE_PATTERN = "/api/v1/**";
    private static final String GRAPHQL_PATTERN = "/graphql";

    private static final String[] EXCLUDED_PATTERNS = {
            "/api/v1/auth/**",
            "**/docs",
            "/api/swagger-ui/**",
            "/api/v1/v3/api-docs/**"
    };

    private final JwtInterceptor jwtInterceptor;
    private final AdminInterceptor adminInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns(API_BASE_PATTERN, GRAPHQL_PATTERN)
                .excludePathPatterns(EXCLUDED_PATTERNS);

        registry.addInterceptor(adminInterceptor)
                .addPathPatterns(API_BASE_PATTERN)
                .excludePathPatterns(EXCLUDED_PATTERNS);
    }
}

package com.example.ecommerce_system.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@AllArgsConstructor
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;
    private final AdminInterceptor adminInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/v1/**", "/graphql")
                .excludePathPatterns(
                        "/api/v1/auth/**",
                        "**/docs",
                        "/api/swagger-ui/**",
                        "/api/v1/v3/api-docs/**"
                );

        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/api/v1/**")
                .excludePathPatterns(
                        "/api/v1/auth/**",
                        "**/docs",
                        "/api/swagger-ui/**",
                        "/api/v1/v3/api-docs/**"
                );
    }
}

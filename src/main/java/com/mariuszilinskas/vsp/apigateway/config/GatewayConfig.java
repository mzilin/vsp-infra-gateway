package com.mariuszilinskas.vsp.apigateway.config;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;

@Configuration
@RequiredArgsConstructor
public class GatewayConfig {

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(p -> p
                        .path("/auth/**")
                        .filters(f -> applyServiceFilters(f, "/auth/(?<segment>.*)"))
                        .uri("lb://AUTH"))
                .route(p -> p
                        .path("/users/**")
                        .filters(f -> applyServiceFilters(f, "/users/(?<segment>.*)"))
                        .uri("lb://USERS"))
                .build();
    }

    private GatewayFilterSpec applyServiceFilters(GatewayFilterSpec filterSpec, String path) {
        return filterSpec
                .rewritePath(path, "/${segment}")
                .removeRequestHeader("Authorization")
                .addResponseHeader("X-Response-Time", new Date().toString());
    }

}

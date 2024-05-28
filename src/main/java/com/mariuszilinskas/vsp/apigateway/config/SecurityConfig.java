package com.mariuszilinskas.vsp.apigateway.config;

import com.mariuszilinskas.vsp.apigateway.enums.UserRole;
import com.mariuszilinskas.vsp.apigateway.util.AppUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.reactive.CorsConfigurationSource;

@Configuration
@EnableMethodSecurity
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity httpSecurity) {
        return httpSecurity
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(authorize -> authorize
                        .pathMatchers(AppUtils.getPublicAccessPaths().toArray(new String[0])).permitAll()
                        .pathMatchers(AppUtils.getAdminOnlyPaths().toArray(new String[0]))
                            .hasAuthority(UserRole.ROLE_ADMIN.name())
                        .anyExchange().authenticated()
                )
                .build();
    }
}

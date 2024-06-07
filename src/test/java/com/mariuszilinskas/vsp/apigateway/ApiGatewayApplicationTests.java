package com.mariuszilinskas.vsp.apigateway;

import com.mariuszilinskas.vsp.apigateway.config.CorsConfig;
import com.mariuszilinskas.vsp.apigateway.config.GatewayConfig;
import com.mariuszilinskas.vsp.apigateway.config.SecurityConfig;
import com.mariuszilinskas.vsp.apigateway.filter.AuthenticationFilter;
import com.mariuszilinskas.vsp.apigateway.service.JwtServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ApiGatewayApplicationTests {

    @Autowired
    private CorsConfig corsConfig;

    @Autowired
    private GatewayConfig gatewayConfig;

    @Autowired
    private SecurityConfig securityConfig;

    @Autowired
    private AuthenticationFilter authenticationFilter;

    @Autowired
    private JwtServiceImpl jwtService;

    @Test
    void contextLoads() {
    }

    // -----------------------------------------------------

    @Test
    void corsConfigBeanLoads() {
        assertNotNull(corsConfig, "Cors Config should have been auto-wired by Spring Context");
    }

    @Test
    void gatewayConfigBeanLoads() {
        assertNotNull(gatewayConfig, "Gateway Config should have been auto-wired by Spring Context");
    }

    @Test
    void securityConfigBeanLoads() {
        assertNotNull(securityConfig, "Security Config should have been auto-wired by Spring Context");
    }

    @Test
    void authenticationFilterBeanLoads() {
        assertNotNull(authenticationFilter, "Authentication Filter should have been auto-wired by Spring Context");
    }

    @Test
    void jwtServiceBeanLoads() {
        assertNotNull(jwtService, "Jwt Service should have been auto-wired by Spring Context");
    }

}

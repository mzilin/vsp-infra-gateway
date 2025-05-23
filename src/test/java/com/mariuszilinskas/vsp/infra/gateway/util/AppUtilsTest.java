package com.mariuszilinskas.vsp.infra.gateway.util;

import org.junit.jupiter.api.Test;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;

import static org.junit.jupiter.api.Assertions.*;

public class AppUtilsTest {

    @Test
    void testIsPublicPath_StaticPublicPaths() {
        assertTrue(AppUtils.isPublicPath.test(MockServerHttpRequest.get("/").build()));
        assertTrue(AppUtils.isPublicPath.test(MockServerHttpRequest.get("/actuator/health").build()));
        assertTrue(AppUtils.isPublicPath.test(MockServerHttpRequest.get("/actuator/refresh").build()));
        assertTrue(AppUtils.isPublicPath.test(MockServerHttpRequest.get("/auth/credentials").build()));
        assertTrue(AppUtils.isPublicPath.test(MockServerHttpRequest.get("/auth/login").build()));
        assertTrue(AppUtils.isPublicPath.test(MockServerHttpRequest.get("/auth/token").build()));
        assertTrue(AppUtils.isPublicPath.test(MockServerHttpRequest.get("/users/register").build()));
        assertTrue(AppUtils.isPublicPath.test(MockServerHttpRequest.get("/users/password/forgot").build()));
        assertTrue(AppUtils.isPublicPath.test(MockServerHttpRequest.get("/users/password/reset").build()));
    }

    @Test
    void testIsPublicPath_StaticPublicPaths_V1() {
        assertTrue(AppUtils.isPublicPath.test(MockServerHttpRequest.get("/").build()));
        assertTrue(AppUtils.isPublicPath.test(MockServerHttpRequest.get("/api/v1/actuator/health").build()));
        assertTrue(AppUtils.isPublicPath.test(MockServerHttpRequest.get("/api/v1/actuator/refresh").build()));
        assertTrue(AppUtils.isPublicPath.test(MockServerHttpRequest.get("/api/v1/auth/credentials").build()));
        assertTrue(AppUtils.isPublicPath.test(MockServerHttpRequest.get("/api/v1/auth/login").build()));
        assertTrue(AppUtils.isPublicPath.test(MockServerHttpRequest.get("/api/v1/auth/token").build()));
        assertTrue(AppUtils.isPublicPath.test(MockServerHttpRequest.get("/api/v1/users/register").build()));
        assertTrue(AppUtils.isPublicPath.test(MockServerHttpRequest.get("/api/v1/users/password/forgot").build()));
        assertTrue(AppUtils.isPublicPath.test(MockServerHttpRequest.get("/api/v1/users/password/reset").build()));
    }

    @Test
    void testIsPublicPath_DynamicPrivatePaths() {
        assertFalse(AppUtils.isPublicPath.test(MockServerHttpRequest.get("/auth/password/verify").build()));
        assertFalse(AppUtils.isPublicPath.test(MockServerHttpRequest.get("/auth/passcode/_USER_ID_/verify").build()));
        assertFalse(AppUtils.isPublicPath.test(MockServerHttpRequest.get("/users/_USER_ID_").build()));
    }
}

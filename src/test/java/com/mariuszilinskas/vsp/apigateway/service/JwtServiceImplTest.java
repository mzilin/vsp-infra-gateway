package com.mariuszilinskas.vsp.apigateway.service;

import com.mariuszilinskas.vsp.apigateway.dto.JwtPayload;
import com.mariuszilinskas.vsp.apigateway.exception.JwtTokenValidationException;
import com.mariuszilinskas.vsp.apigateway.util.AppUtils;
import com.mariuszilinskas.vsp.apigateway.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;

import java.lang.reflect.Field;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtServiceImplTest {

    @Mock
    private ServerWebExchange exchange;

    @Mock
    private ServerHttpRequest request;

    @Mock
    private HttpCookie httpCookie;

    @InjectMocks
    private JwtServiceImpl jwtService;

    private static final String secretKey = TestUtils.secretKey;
    private static final UUID userId = TestUtils.userId;
    private static final String validAccessToken = TestUtils.validAccessToken;
    private static MultiValueMap<String, HttpCookie> cookies;

    // ------------------------------------

    @BeforeEach
    void setup() throws NoSuchFieldException, IllegalAccessException {
        Field field = jwtService.getClass().getDeclaredField("accessTokenSecret");
        field.setAccessible(true);
        field.set(jwtService, secretKey);

        cookies = new LinkedMultiValueMap<>();
        cookies.add(AppUtils.ACCESS_TOKEN_NAME, httpCookie);
    }

    // ------------------------------------

    @Test
    void testExtractPayload_ValidToken() {
        // Arrange
        when(exchange.getRequest()).thenReturn(request);
        when(request.getCookies()).thenReturn(cookies);
        when(httpCookie.getValue()).thenReturn(validAccessToken);

        // Act
        Optional<JwtPayload> response = jwtService.extractPayload(exchange);

        // Assert
        assertTrue(response.isPresent());
        JwtPayload payload = response.get();
        assertEquals(userId.toString(), payload.userId());
        assertEquals(List.of("USER", "ADMIN"), payload.roles());
        assertEquals(List.of("MANAGE_SETTINGS"), payload.authorities());
        assertEquals(new Date(3295380670000L), payload.expiry());
    }

    @Test
    void testExtractPayload_InvalidToken() {
        // Arrange
        when(exchange.getRequest()).thenReturn(request);
        when(request.getCookies()).thenReturn(cookies);
        when(httpCookie.getValue()).thenReturn("invalidAccessToken");

        // Act & Assert
        assertThrows(JwtTokenValidationException.class, () -> jwtService.extractPayload(exchange));
    }

    // ------------------------------------

    @Test
    void testSafelyExtractListFromClaims() {
        // Arrange
        Map<String, Object> claims = Map.of(
                "roles", List.of("USER", "ADMIN"),
                "authorities", List.of("MANAGE_SETTINGS")
        );

        // Act
        List<String> roles = jwtService.safelyExtractListFromClaims(claims, "roles");
        List<String> authorities = jwtService.safelyExtractListFromClaims(claims, "authorities");

        // Assert
        assertEquals(List.of("USER", "ADMIN"), roles);
        assertEquals(List.of("MANAGE_SETTINGS"), authorities);
    }

    // ------------------------------------

    @Test
    void testExtractAccessToken_ValidToken() {
        // Arrange
        when(exchange.getRequest()).thenReturn(request);
        when(request.getCookies()).thenReturn(cookies);
        when(httpCookie.getValue()).thenReturn(validAccessToken);

        // Act
        String response = jwtService.extractAccessToken(exchange);

        // Assert
        assertNotNull(response);
        assertEquals(validAccessToken, response);
    }

    @Test
    void testExtractAccessToken_InvalidToken() {
        // Arrange
        when(exchange.getRequest()).thenReturn(request);
        when(request.getCookies()).thenReturn(cookies);
        when(httpCookie.getValue()).thenReturn("invalidAccessToken");

        // Act & Assert
        assertThrows(JwtTokenValidationException.class, () -> jwtService.extractUserId(exchange));
    }

    // ------------------------------------

    @Test
    void testExtractUserId_ValidToken() {
        // Arrange
        when(exchange.getRequest()).thenReturn(request);
        when(request.getCookies()).thenReturn(cookies);
        when(httpCookie.getValue()).thenReturn(validAccessToken);

        // Act
        UUID response = jwtService.extractUserId(exchange);

        // Assert
        assertNotNull(response);
        assertEquals(userId, response);
    }

    @Test
    void testExtractUserId_InvalidToken() {
        // Arrange
        when(exchange.getRequest()).thenReturn(request);
        when(request.getCookies()).thenReturn(cookies);
        when(httpCookie.getValue()).thenReturn("invalidAccessToken");

        // Act & Assert
        assertThrows(JwtTokenValidationException.class, () -> jwtService.extractUserId(exchange));
    }

}

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

    // ------------------------------------

    @BeforeEach
    void setup() throws NoSuchFieldException, IllegalAccessException {
        setPrivateField(jwtService, "accessTokenSecret", secretKey);
    }

    private void setPrivateField(Object targetObject, String fieldName, Object value)
            throws NoSuchFieldException, IllegalAccessException {
        Field field = targetObject.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(targetObject, value);
    }

    // ------------------------------------

    @Test
    void testExtractPayload_ValidToken() {
        // Arrange
        when(exchange.getRequest()).thenReturn(request);
        MultiValueMap<String, HttpCookie> cookies = new LinkedMultiValueMap<>();
        cookies.add(AppUtils.ACCESS_TOKEN_NAME, httpCookie);

        when(request.getCookies()).thenReturn(cookies);
        when(httpCookie.getValue()).thenReturn(validAccessToken);

        // Act
        Optional<JwtPayload> result = jwtService.extractPayload(exchange);

        // Assert
        assertTrue(result.isPresent());
        JwtPayload payload = result.get();
        assertEquals(userId.toString(), payload.userId());
        assertEquals(List.of("USER", "ADMIN"), payload.roles());
        assertEquals(List.of("MANAGE_SETTINGS"), payload.authorities());
        assertEquals(new Date(3295380670000L), payload.expiry());
    }

    @Test
    void testExtractPayload_InvalidToken() {
        // Arrange
        when(exchange.getRequest()).thenReturn(request);
        MultiValueMap<String, HttpCookie> cookies = new LinkedMultiValueMap<>();
        cookies.add(AppUtils.ACCESS_TOKEN_NAME, httpCookie);

        when(request.getCookies()).thenReturn(cookies);
        when(httpCookie.getValue()).thenReturn("invalidAccessToken");

        // Act & Assert
        assertThrows(JwtTokenValidationException.class, () -> jwtService.extractPayload(exchange));
    }

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

}

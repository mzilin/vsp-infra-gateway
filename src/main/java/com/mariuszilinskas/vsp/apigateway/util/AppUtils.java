package com.mariuszilinskas.vsp.apigateway.util;

import org.springframework.http.server.reactive.ServerHttpRequest;

import java.util.List;
import java.util.function.Predicate;

public abstract class AppUtils {

    private AppUtils() {
        // Private constructor to prevent instantiation
    }

    // ------------------------------------------------------

    public static final String TIMESTAMP_FORMAT = "yyyy-MM-dd hh:mm:ss";

    public static final String ACCESS_TOKEN_NAME = "vsp_access";

    // ------------------------------------------------------

    private static final List<String> publicAccessPaths = List.of(
            "/",
            "/actuator/health",
            "/actuator/refresh",
            "/auth/credentials",
            "/auth/login",
            "/auth/token",
            "/users/register",
            "/users/password/forgot",
            "/users/password/reset"
    );

    public static List<String> getPublicAccessPaths() {
        return publicAccessPaths;
    }

    public static Predicate<ServerHttpRequest> isPublicPath =
            request -> getPublicAccessPaths().stream()
                    .map(uri -> uri.endsWith("/**") ? uri.substring(0, uri.length() - 2) : uri)
                    .anyMatch(uri -> !"/".equals(uri) && request.getURI().getPath().contains(uri));

}

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

    public static final String USER_ID = "_USER_ID_";

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

    public static String[] getPublicAccessPaths() {
        return publicAccessPaths.toArray(new String[0]);
    }

    public static String[] getAdminAccessPaths() {
        return List.of(
                "/users/admin/**"
        ).toArray(new String[0]);
    }

    public static Predicate<ServerHttpRequest> isPublicPath =
            request -> {
                String path = request.getURI().getPath();
                return publicAccessPaths.stream()
                        .map(uri -> uri.endsWith("/**") ? uri.substring(0, uri.length() - 2) : uri)
                        .anyMatch(uri -> path.equals(uri) || path.startsWith(uri + "/"));
            };

}

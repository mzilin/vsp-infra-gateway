package com.mariuszilinskas.vsp.apigateway.util;

import org.springframework.http.server.reactive.ServerHttpRequest;

import java.util.List;
import java.util.function.Predicate;

public abstract class AppUtils {

    private AppUtils() {
        // Private constructor to prevent instantiation
    }

    // ------------------------------------------------------

    public static final String API_PREFIX = "/api/v1";

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

    private static final List<String> adminAccessPaths = List.of(
            "/users/admin/**",
            "/user-profiles/admin/avatars/**"
    );

    public static String[] getPublicAccessPaths() {
        return prefixPaths(publicAccessPaths, true);
    }

    public static String[] getAdminAccessPaths() {
        return prefixPaths(adminAccessPaths, false);
    }

    private static String[] prefixPaths(List<String> paths, boolean skipRoot) {
        return paths.stream()
                .map(path -> (skipRoot && "/".equals(path)) ? path : AppUtils.API_PREFIX + path)
                .toArray(String[]::new);
    }

    public static Predicate<ServerHttpRequest> isPublicPath =
            request -> {
                String path = request.getURI().getPath().replace(API_PREFIX, "");
                return publicAccessPaths.stream()
                        .map(uri -> uri.endsWith("/**") ? uri.substring(0, uri.length() - 2) : uri)
                        .anyMatch(uri -> path.equals(uri) || path.startsWith(uri + "/"));
            };

}

package com.mariuszilinskas.vsp.apigateway.util;

import java.util.List;

public abstract class AppUtils {

    private AppUtils() {
        // Private constructor to prevent instantiation
    }

    // ------------------------------------------------------

    public static final String TIMESTAMP_FORMAT = "yyyy-MM-dd hh:mm:ss";

    // ------------------------------------------------------

    public static List<String> getPublicAccessPaths() {
        return List.of(
                "/",
                "/actuator/health",
                "/actuator/refresh",
                "/auth/credentials",
                "/users/register"
        );
    }

    public static List<String> getAdminOnlyPaths() {
        return List.of(

        );
    }

}

package com.mariuszilinskas.vsp.apigateway.exception;

/**
 * This class represents a custom exception to be thrown when
 * refresh token expires
 *
 * @author Marius Zilinskas
 */
public class SessionExpiredException extends RuntimeException {

    public SessionExpiredException() {
        super("Session expired");
    }
}

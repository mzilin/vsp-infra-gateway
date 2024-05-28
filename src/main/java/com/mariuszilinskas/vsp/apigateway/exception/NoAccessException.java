package com.mariuszilinskas.vsp.apigateway.exception;

/**
 * This class represents a custom exception to be thrown when
 * User has no access to retrieve the resource
 *
 * @author Marius Zilinskas
 */
public class NoAccessException extends RuntimeException {

    public NoAccessException() {
        super("This request is forbidden");
    }
}

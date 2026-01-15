package com.exposer.constants;


public final class ErrorMessage {

    private ErrorMessage() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static final String ACCESS_DENIED = "You don't have permission to perform these actions";
    public static final String INVALID_USERNAME_PASSWORD = "Invalid username or password";
    public static final String RESOURCE_NOT_FOUND_MESSAGE = "Resource not found";
    public static final String UNAUTHENTICATED_ILLEGAL_MESSAGE = "We don't allow you to perform these actions";
    public static final String STATUS_ERROR = "error";
    public static final String STATUS_SUCCESS = "success";


}

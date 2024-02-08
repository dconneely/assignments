package com.davidconneely.purchase.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;

public class TooManyDecimalPlacesException extends ResponseStatusException {
    private static final URI URI_TOO_MANY_DECIMAL_PLACES = URI.create("https://purchase.davidconneely.com/too-many-decimal-places");

    public TooManyDecimalPlacesException(String detail) {
        super(HttpStatus.BAD_REQUEST, detail);
        setType(URI_TOO_MANY_DECIMAL_PLACES);
    }
}

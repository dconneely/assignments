package com.davidconneely.purchase.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;

public class ResourceNotFoundException extends ResponseStatusException {
    private static final URI URI_RESOURCE_NOT_FOUND = URI.create("https://purchase.davidconneely.com/resource-not-found");

    public ResourceNotFoundException(String detail) {
        super(HttpStatus.NOT_FOUND, detail);
        setType(URI_RESOURCE_NOT_FOUND);
    }
}

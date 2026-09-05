package com.davidconneely.purchase.exception;

import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class RateNotAvailableException extends ResponseStatusException {
  private static final URI URI_RATE_NOT_AVAILABLE =
      URI.create("https://purchase.davidconneely.com/rate-not-available");

  public RateNotAvailableException(String detail) {
    super(HttpStatus.FAILED_DEPENDENCY, detail);
    setType(URI_RATE_NOT_AVAILABLE);
  }
}

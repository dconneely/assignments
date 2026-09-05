package com.davidconneely.purchase.exception;

import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class PurchaseNotFoundException extends ResponseStatusException {
  private static final URI URI_PURCHASE_NOT_FOUND =
      URI.create("https://purchase.davidconneely.com/purchase-not-found");

  public PurchaseNotFoundException(String detail) {
    super(HttpStatus.NOT_FOUND, detail);
    setType(URI_PURCHASE_NOT_FOUND);
  }
}

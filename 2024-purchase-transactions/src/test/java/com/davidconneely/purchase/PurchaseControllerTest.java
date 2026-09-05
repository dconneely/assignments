package com.davidconneely.purchase;

import static com.davidconneely.purchase.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import com.davidconneely.purchase.dto.IdResponse;
import com.davidconneely.purchase.dto.PurchaseRequest;
import com.davidconneely.purchase.dto.PurchaseResponse;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public final class PurchaseControllerTest {
  @Test
  public void testStoreTransaction() {
    PurchaseService service = mock(PurchaseService.class);
    when(service.create(any(PurchaseRequest.class))).thenReturn(ID_GOOD);

    // so ServletUriComponentsBuilder static methods work:
    MockHttpServletRequest request = new MockHttpServletRequest();
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

    PurchaseController controller = new PurchaseController(service);
    ResponseEntity<IdResponse> response = controller.storeTransaction(newGoodRequest());
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(ID_GOOD, Objects.requireNonNull(response.getBody()).id());
    assertTrue(
        Objects.requireNonNull(response.getHeaders().getLocation())
            .getPath()
            .contains(ID_GOOD.toString()));
  }

  @Test
  public void testRetrieveTransaction() {
    PurchaseService service = mock(PurchaseService.class);
    when(service.get(any(UUID.class), anyString())).thenReturn(Optional.of(newGoodResponse()));

    PurchaseController controller = new PurchaseController(service);
    ResponseEntity<PurchaseResponse> response =
        controller.retrieveTransaction(ID_GOOD, COUNTRY_CURRENCY_DESC_UK2015);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(newGoodResponse(), response.getBody());
  }
}

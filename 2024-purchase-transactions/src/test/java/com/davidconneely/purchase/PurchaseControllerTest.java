package com.davidconneely.purchase;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static com.davidconneely.purchase.TestUtils.newGoodDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public final class PurchaseControllerTest {
    @Test
    public void testStoreTransaction() {
        PurchaseService service = mock(PurchaseService.class);
        final UUID id = UUID.randomUUID();
        when(service.create(any(PurchaseDto.class))).thenReturn(id);

        // so ServletUriComponentsBuilder static methods work:
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        PurchaseController controller = new PurchaseController(service);
        ResponseEntity<PurchaseDto> response = controller.storeTransaction(newGoodDto());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(newGoodDto(), response.getBody());
        assertTrue(Objects.requireNonNull(response.getHeaders().getLocation()).getPath().contains(id.toString()));
    }

    @Test
    public void testRetrieveTransaction() {
        PurchaseService service = mock(PurchaseService.class);
        when(service.get(any(UUID.class))).thenReturn(Optional.of(newGoodDto()));

        PurchaseController controller = new PurchaseController(service);
        final UUID id = UUID.randomUUID();
        ResponseEntity<PurchaseDto> response = controller.retrieveTransaction(id);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(newGoodDto(), response.getBody());
    }
}
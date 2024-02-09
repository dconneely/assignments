package com.davidconneely.purchase;

import com.davidconneely.purchase.client.RatesOfExchangeClient;
import com.davidconneely.purchase.dao.PurchaseEntity;
import com.davidconneely.purchase.dao.PurchaseRepository;
import com.davidconneely.purchase.dto.PurchaseConverter;
import com.davidconneely.purchase.dto.PurchaseRequest;
import com.davidconneely.purchase.dto.PurchaseResponse;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static com.davidconneely.purchase.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class PurchaseServiceTest {
    @Test
    public void testCreate() {
        PurchaseRepository repository = mock(PurchaseRepository.class);
        when(repository.save(any(PurchaseEntity.class))).thenAnswer(input -> {
            PurchaseEntity entity = input.getArgument(0);
            assertEquals(newGoodEntity(), entity);
            entity.setId(ID_GOOD);
            return entity;
        });
        PurchaseConverter converter = mock(PurchaseConverter.class);
        when(converter.entityFromRequest(any(PurchaseRequest.class))).thenCallRealMethod();
        RatesOfExchangeClient client = mock(RatesOfExchangeClient.class);
        when(client.getSingleRate(anyString(), any(LocalDate.class))).thenReturn(EXCHANGE_RATE_GOOD);

        PurchaseService service = new PurchaseService(repository, converter, client);
        UUID id = service.create(newGoodRequest());
        assertEquals(ID_GOOD, id);
    }

    @Test
    public void testGet() {
        PurchaseRepository repository = mock(PurchaseRepository.class);
        when(repository.findById(any(UUID.class))).thenAnswer(input -> {
            UUID id = input.getArgument(0);
            assertEquals(ID_GOOD, id);
            PurchaseEntity entity = newGoodEntity();
            entity.setId(id);
            return Optional.of(entity);
        });
        PurchaseConverter converter = mock(PurchaseConverter.class);
        when(converter.responseFromEntity(any(PurchaseEntity.class), any(BigDecimal.class))).thenCallRealMethod();
        when(converter.convertedAmount(any(BigDecimal.class), any(BigDecimal.class))).thenCallRealMethod();
        RatesOfExchangeClient client = mock(RatesOfExchangeClient.class);
        when(client.getSingleRate(anyString(), any(LocalDate.class))).thenReturn(EXCHANGE_RATE_GOOD);

        PurchaseService service = new PurchaseService(repository, converter, client);
        PurchaseResponse dto = service.get(ID_GOOD, COUNTRY_CURRENCY_DESC_UK2015).orElse(null);
        assertEquals(newGoodResponse(), dto);
    }
}

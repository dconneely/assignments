package com.davidconneely.purchase;

import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static com.davidconneely.purchase.TestUtils.newGoodDto;
import static com.davidconneely.purchase.TestUtils.newGoodEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class PurchaseServiceTest {
    private PurchaseRepository mockRepository;
    private PurchaseService purchaseService;

    @Test
    public void testCreate() {
        PurchaseRepository repository = mock(PurchaseRepository.class);
        final UUID goodId = UUID.randomUUID();
        when(repository.save(any(PurchaseEntity.class))).thenAnswer(input -> {
            PurchaseEntity entity = input.getArgument(0);
            assertEquals(newGoodEntity(), entity);
            entity.setId(goodId);
            return entity;
        });
        PurchaseService service = new PurchaseService(repository);
        UUID id = service.create(newGoodDto());
        assertEquals(goodId, id);
    }

    @Test
    public void testGet() {
        PurchaseRepository repository = mock(PurchaseRepository.class);
        final UUID goodId = UUID.randomUUID();
        when(repository.findById(any(UUID.class))).thenAnswer(input -> {
            UUID id = input.getArgument(0);
            assertEquals(goodId, id);
            PurchaseEntity entity = newGoodEntity();
            entity.setId(id);
            return Optional.of(entity);
        });
        PurchaseService service = new PurchaseService(repository);
        PurchaseDto dto = service.get(goodId).orElse(null);
        assertEquals(newGoodDto(), dto);
    }
}

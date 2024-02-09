package com.davidconneely.purchase;

import com.davidconneely.purchase.client.RatesOfExchangeClient;
import com.davidconneely.purchase.dao.PurchaseEntity;
import com.davidconneely.purchase.dao.PurchaseRepository;
import com.davidconneely.purchase.dto.PurchaseConverter;
import com.davidconneely.purchase.dto.PurchaseRequest;
import com.davidconneely.purchase.dto.PurchaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PurchaseService {
    private final PurchaseRepository repository;
    private final PurchaseConverter converter;
    private final RatesOfExchangeClient client;

    public UUID create(PurchaseRequest dto) {
        PurchaseEntity entity = converter.entityFromRequest(dto);
        repository.save(entity);
        return entity.getId();
    }

    public Optional<PurchaseResponse> get(UUID id, String countryCurrencyDesc) {
        return repository.findById(id).map(entity -> new PurchaseConverter()
                .responseFromEntity(entity, client.getSingleRate(countryCurrencyDesc, entity.getTransactionDate())));
    }
}

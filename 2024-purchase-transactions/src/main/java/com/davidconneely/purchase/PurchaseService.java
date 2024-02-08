package com.davidconneely.purchase;

import com.davidconneely.purchase.client.RatesOfExchangeClient;
import com.davidconneely.purchase.dao.PurchaseEntity;
import com.davidconneely.purchase.dao.PurchaseRepository;
import com.davidconneely.purchase.dto.PurchaseConverter;
import com.davidconneely.purchase.dto.PurchaseRequest;
import com.davidconneely.purchase.dto.PurchaseResponse;
import com.davidconneely.purchase.dto.RatesOfExchangeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
        Optional<PurchaseEntity> opt = repository.findById(id);
        // TODO: This uses a hardcoded exchange rate of 1.2864 for now (to make tests work).
        return opt.map(entity -> new PurchaseConverter().responseFromEntity(entity, client.fetchRate(countryCurrencyDesc, entity.getTransactionDate())));
    }
}

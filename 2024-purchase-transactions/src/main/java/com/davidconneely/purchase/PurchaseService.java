package com.davidconneely.purchase;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PurchaseService {
    private final PurchaseRepository repository;

    public UUID create(PurchaseDto dto) {
        PurchaseEntity entity = new PurchaseEntity();
        BeanUtils.copyProperties(dto, entity);
        repository.save(entity);
        return entity.getId();
    }

    public Optional<PurchaseDto> get(UUID id) {
        Optional<PurchaseEntity> optional = repository.findById(id);
        return optional.map(entity -> new PurchaseDto(entity.getDescription(), entity.getTransactionDate(), entity.getPurchaseAmount()));
    }
}

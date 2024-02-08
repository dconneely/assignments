package com.davidconneely.purchase;

import com.davidconneely.purchase.dto.IdResponse;
import com.davidconneely.purchase.dto.PurchaseRequest;
import com.davidconneely.purchase.dto.PurchaseResponse;
import com.davidconneely.purchase.exception.ResourceNotFoundException;
import com.davidconneely.purchase.exception.TooManyDecimalPlacesException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/purchase")
@RequiredArgsConstructor
public class PurchaseController {
    private final PurchaseService service;

    @PostMapping(value = {"", "/"})
    public ResponseEntity<IdResponse> storeTransaction(@NotNull @RequestBody PurchaseRequest dto) {
        log.info("#storeTransaction(" + dto + ")");

        BigDecimal purchaseAmount = dto.purchaseAmount();
        if (purchaseAmount.compareTo(purchaseAmount.setScale(2, RoundingMode.HALF_UP)) != 0) {
            throw new TooManyDecimalPlacesException("purchaseAmount: is not rounded to the nearest cent");
        }

        UUID id = service.create(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(id).toUri();
        return ResponseEntity.created(location).body(new IdResponse(id));
    }

    @GetMapping(value = {"/{id}", "/{id}/"})
    public ResponseEntity<PurchaseResponse> retrieveTransaction(@NotNull @PathVariable("id") UUID id, @RequestParam("country_currency_desc") String countryCurrencyDesc) {
        log.info("#retrieveTransaction(" + id + ")");
        return ResponseEntity.ok(service.get(id, countryCurrencyDesc).orElseThrow(() -> new ResourceNotFoundException("UUID provided in URI does not correspond to any stored purchase transaction")));
    }
}

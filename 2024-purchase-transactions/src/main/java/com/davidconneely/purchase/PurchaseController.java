package com.davidconneely.purchase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/purchase")
@RequiredArgsConstructor
public class PurchaseController {
    private final PurchaseService purchaseService;

    @PostMapping
    public ResponseEntity<PurchaseDto> storeTransaction(@RequestBody PurchaseDto dto) {
        log.debug("storeTransaction(" + dto + ")");
        if (dto == null) {
            return ResponseEntity.badRequest().build();
        }
        // TODO validate the DTO
        UUID id = purchaseService.create(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(id).toUri();
        return ResponseEntity.created(location).body(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseDto> retrieveTransaction(@PathVariable("id") UUID id) {
        log.debug("retrieveTransaction(" + id + ")");
        if (id == null) {
            return ResponseEntity.badRequest().build();
        }
        Optional<PurchaseDto> optional = purchaseService.get(id);
        return ResponseEntity.of(optional);
    }
}

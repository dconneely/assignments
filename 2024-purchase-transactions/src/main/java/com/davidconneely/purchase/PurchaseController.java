package com.davidconneely.purchase;

import com.davidconneely.purchase.dto.IdResponse;
import com.davidconneely.purchase.dto.PurchaseRequest;
import com.davidconneely.purchase.dto.PurchaseResponse;
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
    private final PurchaseService service;

    @PostMapping
    public ResponseEntity<IdResponse> storeTransaction(@RequestBody PurchaseRequest dto) {
        log.info("#storeTransaction(" + dto + ")");
        if (dto == null || !dto.isValid()) {
            return ResponseEntity.badRequest().build();
        }
        UUID id = service.create(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(id).toUri();
        return ResponseEntity.created(location).body(new IdResponse(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseResponse> retrieveTransaction(@PathVariable("id") UUID id) {
        log.info("#retrieveTransaction(" + id + ")");
        if (id == null) {
            return ResponseEntity.badRequest().build();
        }
        Optional<PurchaseResponse> opt = service.get(id);
        return ResponseEntity.of(opt);
    }
}

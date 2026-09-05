package com.davidconneely.purchase;

import com.davidconneely.purchase.dto.IdResponse;
import com.davidconneely.purchase.dto.PurchaseRequest;
import com.davidconneely.purchase.dto.PurchaseResponse;
import com.davidconneely.purchase.exception.PurchaseNotFoundException;
import com.davidconneely.purchase.exception.TooManyDecimalPlacesException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Slf4j
@RestController
@RequestMapping("/purchase")
@RequiredArgsConstructor
public class PurchaseController {
  private final PurchaseService service;

  @PostMapping({"", "/"})
  @Operation(
      summary = "Stores a purchase transaction",
      responses = {
        @ApiResponse(
            responseCode = "201",
            description =
                "Created. Returns a transaction ID that can later be used to retrieve this stored purchase",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = IdResponse.class))),
        @ApiResponse(
            responseCode = "400",
            description =
                "Bad Request. Invalid input values, for example if the transactionDate is not parseable, or the purchaseAmount is negative",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class))),
      })
  public ResponseEntity<IdResponse> storeTransaction(@NotNull @RequestBody PurchaseRequest dto) {
    log.debug("#storeTransaction(" + dto + ")");

    BigDecimal purchaseAmount = dto.purchaseAmount();
    if (purchaseAmount.compareTo(purchaseAmount.setScale(2, RoundingMode.HALF_UP)) != 0) {
      throw new TooManyDecimalPlacesException("purchaseAmount: is not rounded to the nearest cent");
    }
    UUID id = service.create(dto);
    URI location =
        ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(id).toUri();
    return ResponseEntity.created(location).body(new IdResponse(id));
  }

  @GetMapping({"/{id}", "/{id}/"})
  @Operation(
      summary = "Retrieve a previously-stored purchase transaction, with currency conversion",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description =
                "OK. Response includes the exchangeRate effective at the transaction date of the purchase transaction, and the convertedAmount of foreign currency units (that the purchaseAmount of US dollars corresponds to)",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = PurchaseResponse.class))),
        @ApiResponse(
            responseCode = "400",
            description =
                "Bad Request. Invalid input values, for example if the ID is not a valid UUID",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(
            responseCode = "404",
            description =
                "Not Found. If no stored purchase transaction exists with the specified ID",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(
            responseCode = "424",
            description =
                "Failed Dependency. The Rates of Exchange service could not provide an exchange rate for the selected currency on the transactionDate",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class)))
      })
  public ResponseEntity<PurchaseResponse> retrieveTransaction(
      @Parameter(description = "A previously-stored purchase transaction ID")
          @NotNull
          @PathVariable("id")
          UUID id,
      @Parameter(
              description = "Treasury representation of a country's currency",
              example = "United Kingdom-Pound")
          @RequestParam("country_currency_desc")
          String countryCurrencyDesc) {
    log.debug("#retrieveTransaction(" + id + ", " + countryCurrencyDesc + ")");

    return ResponseEntity.ok(
        service
            .get(id, countryCurrencyDesc)
            .orElseThrow(
                () ->
                    new PurchaseNotFoundException(
                        "UUID provided in URI does not correspond to any stored purchase transaction")));
  }
}

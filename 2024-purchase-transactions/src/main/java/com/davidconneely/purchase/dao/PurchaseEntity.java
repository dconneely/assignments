package com.davidconneely.purchase.dao;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "purchase")
@Data
@NoArgsConstructor
public class PurchaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "description", length = 50)
    @Size(max = 50)
    private String description;

    @Temporal(TemporalType.DATE)
    @Column(name = "transaction_date")
    @NotNull
    private LocalDate transactionDate;

    @Column(name = "purchase_amount")
    @NotNull @Positive
    private BigDecimal purchaseAmount;
}

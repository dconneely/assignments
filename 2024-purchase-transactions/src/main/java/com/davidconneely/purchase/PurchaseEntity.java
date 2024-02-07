package com.davidconneely.purchase;

import jakarta.persistence.*;
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
    private String description;

    @Temporal(TemporalType.DATE)
    @Column(name = "transaction_date")
    private LocalDate transactionDate;

    @Column(name = "purchase_amount")
    private BigDecimal purchaseAmount;
}

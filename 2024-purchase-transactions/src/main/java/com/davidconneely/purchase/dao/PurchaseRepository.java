package com.davidconneely.purchase.dao;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseRepository extends JpaRepository<PurchaseEntity, UUID> {
  // the implementation is an auto-generated proxy of this interface.
}

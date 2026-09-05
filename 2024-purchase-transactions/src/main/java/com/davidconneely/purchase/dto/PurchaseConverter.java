package com.davidconneely.purchase.dto;

import com.davidconneely.purchase.dao.PurchaseEntity;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class PurchaseConverter {
  /**
   * Converts the incoming DTO into an entity that can become a stored purchase transaction.
   *
   * @param dto a DTO with the field supplied from the REST controller.
   * @return an entity ready to be persisted.
   */
  public PurchaseEntity entityFromRequest(PurchaseRequest dto) {
    PurchaseEntity entity = new PurchaseEntity();
    BeanUtils.copyProperties(dto, entity);
    return entity;
  }

  /**
   * Converts a stored purchase transaction and an exchange rate into the required retrieved
   * response.
   *
   * @param entity a stored purchase transaction.
   * @param exchangeRate number of foreign currency units in a US dollar.
   * @return a DTO with the required fields, including a converted amount.
   */
  public PurchaseResponse responseFromEntity(PurchaseEntity entity, BigDecimal exchangeRate) {
    BigDecimal purchaseAmount = entity.getPurchaseAmount();
    BigDecimal convertedAmount = convertedAmount(purchaseAmount, exchangeRate);
    return new PurchaseResponse(
        entity.getId(),
        entity.getDescription(),
        entity.getTransactionDate(),
        purchaseAmount,
        exchangeRate,
        convertedAmount);
  }

  /**
   * Converts from US dollars to another currency using the exchange rate given.
   *
   * @param purchaseAmount an amount in US dollars.
   * @param exchangeRate number of foreign currency units in a US dollar.
   * @return the amount in foreign currency units, with appropriate rounding (to two decimal places
   *     / cents).
   */
  public BigDecimal convertedAmount(BigDecimal purchaseAmount, BigDecimal exchangeRate) {
    return purchaseAmount.multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP);
  }
}

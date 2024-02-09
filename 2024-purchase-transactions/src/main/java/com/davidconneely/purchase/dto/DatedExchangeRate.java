package com.davidconneely.purchase.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Used to keep a cache of exchange rates for a single currency with respect to the US dollar.
 * Because there can only be one exchange rate for a single currency on any given day, the
 * Object#equals, Object#hashCode and Comparable#compareTo overrides only consider the date.
 *
 * @param date         The cache key (date at which the exchange rate applies from).
 * @param exchangeRate The cache value (the exchange rate from that date forward).
 */
public record DatedExchangeRate(LocalDate date, BigDecimal exchangeRate) implements Comparable<DatedExchangeRate> {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DatedExchangeRate that = (DatedExchangeRate) o;
        return Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date);
    }

    @Override
    public int compareTo(DatedExchangeRate o) {
        return date.compareTo(o.date);
    }
}

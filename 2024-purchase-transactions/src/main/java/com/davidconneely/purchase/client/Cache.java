package com.davidconneely.purchase.client;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Objects;
import lombok.Data;

@Data
public class Cache {
  /**
   * Used to keep a cache of exchange rates for a single currency with respect to the US dollar.
   * Because there can only be one exchange rate for a single currency on any given day, the
   * Object#equals, Object#hashCode and Comparable#compareTo overrides only consider the date.
   *
   * @param date The cache key (date at which the exchange rate applies from).
   * @param exchangeRate The cache value (the exchange rate from that date forward).
   */
  public record Entry(LocalDate date, BigDecimal exchangeRate) implements Comparable<Entry> {
    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Entry that = (Entry) o;
      return Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
      return Objects.hash(date);
    }

    @Override
    public int compareTo(Entry o) {
      return date.compareTo(o.date);
    }
  }

  private LocalDate lastUpdateDate;
  private LocalDate lastRecordDate;
  private Map<String, NavigableSet<Entry>> data;
}

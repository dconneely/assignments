package com.davidconneely.purchase.client;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Reading the <a
 * href="https://fiscaldata.treasury.gov/datasets/treasury-reporting-rates-exchange/treasury-reporting-rates-of-exchange#dataset-properties">Data
 * Dictionary</a>, it seems `effective_date` is the correct field to use for conversion, not
 * `record_date`.
 *
 * <p>A new record with the same `record_date` but a later `effective_date` can be added to a
 * quarter if the currency shifts by more than 10% within a quarter, and that new `exchange_rate`
 * should be used from the `effective_date`.
 *
 * <p>There's actually less than 20,000 records in the data-set, so we can easily cache them in
 * memory even if the records were 1 kB each (they are probably just a few 100 bytes each, so will
 * easily fit in less than 10 MB).
 *
 * <p>Note that the API limits the response data size to 10,000 records in a single REST call. Also,
 * data is only ever added to either the current quarter (same `record_date`, but new
 * `effective_date`), or to the new quarter when a new quarter begins.
 *
 * <p>So a valid caching strategy would be to cache everything and just check for new changes since
 * the last quarter or later once a day.
 */
public interface RatesOfExchangeClient {
  BigDecimal getSingleRate(String countryCurrencyDesc, LocalDate transactionDate);
}

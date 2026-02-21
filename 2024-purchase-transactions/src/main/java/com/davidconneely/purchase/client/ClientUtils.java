package com.davidconneely.purchase.client;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class ClientUtils {
  /** Format LocalDate instances into API expected '1999-12-31' String format. */
  static String formatLocalDate(LocalDate date) {
    return DateTimeFormatter.ISO_LOCAL_DATE.format(date);
  }
}

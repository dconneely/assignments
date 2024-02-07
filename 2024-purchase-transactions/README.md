# `purchase`

This application stores and retrieves purchase transactions, with currency
conversion, as described in the `WEX TAG and Gateways Product Brief.pdf`
document.

When run, the application exposes two REST endpoints:

* `POST /purchase/` to store a new purchase transaction.
* `GET /purchase/{id}?currency={currency}` to retrieve a previously-stored
  purchase transaction, with a currency conversion.

# Technical implementation

This is a Spring Boot application built for Java 21, so you should install
a JRE or JDK 21 in order to run or build it respectively.

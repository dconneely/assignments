# `purchase`

This application stores and retrieves purchase transactions, with currency
conversion, as described in the `WEX TAG and Gateways Product Brief.pdf`
document.

When run, the application exposes two REST endpoints:

* `POST /purchase/` to store a new purchase transaction in US dollars.
* `GET /purchase/{id}?country_currency_desc={countryCurrencyDesc}` to retrieve
  a previously-stored purchase transaction, with an additional currency conversion
  from US dollars into the foreign currency.

# Technical implementation

This is a Spring Boot application built for Java 21, so you should install
a JRE or JDK 21 in order to run or build it respectively.

# Roadmap

- [x] Make the services return the correctly-shaped responses.
- [ ] Check that connection to PostgreSQL works as well as just H2 (Docker).
- [ ] Check the default error page isn't revealing stack traces / exceptions.
- [ ] Add a REST client to call the US Treasury exchange rates API.
- [ ] Look into caching the results of the REST client locally to avoid over-calling it.
- [ ] Use real exchange rate data in the retrieve endpoint.
- [ ] Integration tests, more non-happy path tests?
- [ ] Add documentation of the REST service / page to run them.
- [ ] Check for anomalies in the exchange rate data - e.g. case in 2015/16,
      countries that changed currency, Zimbabwe 0.0 rate.
- [ ] Consider integration tests to cover odd cases in the exchange rate data.

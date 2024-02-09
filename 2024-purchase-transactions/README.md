# `purchase`

This application stores and retrieves purchase transactions, with currency
conversion, as described in the `WEX TAG and Gateways Product Brief.pdf`
document.

When run, the application exposes two REST endpoints:

* `POST /purchase/` to store a new purchase transaction in US dollars.
* `GET /purchase/{id}?country_currency_desc={countryCurrencyDesc}` to retrieve
  a previously-stored purchase transaction, with an additional currency conversion
  from US dollars into the foreign currency.

## Technical implementation

This is a Spring Boot application built for Java 21, so a JRE 21 or JDK 21
will need to be installed in order to run or build it respectively.

The repository includes the Maven Wrapper 3.2.0 to automatically download,
install, cache and run Maven 3.9.5 when the `mvnw` or `mvnw.cmd` command-line
scripts are run, so it is not necessary to install Maven separately.

Database schema migration is done using Liquibase, so is database-independent.
Hence the same code can run against both the H2 embedded database (makes it
easy to run the application standalone) or against an external PostgreSQL
server (more appropriate for a production instance).

The application state is only in the database, so the application can be
scaled in production by running multiple instances behind a load-balancer.

## Roadmap for future development

- [X] Look into caching the results of the REST client locally to avoid over-calling it.
- [ ] Integration tests, more non-happy path tests?
- [ ] Use Jakarta-Validation for the check on decimal places
- [X] Add documentation of the REST service / test page to call the services.
- [ ] Consider integration tests to cover odd cases in the exchange rate data.
- [ ] GitHub Actions to run tests and security scan on each push.
- [ ] Add documentation on how to run dev / prod (maybe using Maven/Spring profiles?)

## Anomalies in the Treasury.gov Rates of Exchange data

1. `country_currency_desc` is not consistent even for countries that have never
   changed name nor currency.\
   Example: GBP is `UNITED KINGDOM-POUND STERLING` in 2015-03-31 thru 2019-09-30,
   but `United Kingdom-Pound` before and after this period.
2. `country` is not consistent even if comparing names case-insensitively.\
   Example: it looks like `Antigua & Barbuda` should have been referred to as
   `ANTIGUA-BARBUDA` between 2015-03-31 and 2019-09-30. However, some kind of parsing
   error has led to the `country` name being truncated to `ANTIGUA` (and `BARBUDA-`
   has been prefixed erroneously to the `currency` name in this same period).
3. Some countries change `currency` over time (the actual currency changes, not just
   the name used in the data) and may have multiple currencies over a period.\
   Example 1: `Germany` in 2001-03-31 thru 2002-03-31 (and 2004-09-30 thru 2008-03-31)
   has `Euro` and `Mark` currencies.\
   Example 2: `Venezuela` has `Bolivar Soberano` and `Fuerte (OLD)` in 2019-12-31
   thru 2023-12-31; `BOLIVAR-FUERTE` and `BOLIVAR-SOBERANO` in 2018-09-30 thru
   2019-09-30; `BOLIVAR` in 2015-03-31 thru 2018-06-30; `Soberano (OLD)` in
   2001-03-31 thru 2014-12-31.
4. Currency names may or may not uniquely identify a currency without qualifying
   the currency with the country.\
   Example 1: Many countries use the `Dollar`, `Peso`, `Pound` or `Shilling`
   but these are not the same currencies.\
   Example 2: Many countries use the `Euro` or `E. Caribbean Dollar` and these
   _are_ in fact the same currencies.
5. There are some unexplained large gaps in the data.\
   Example: No rates for `St. Lucia` for over 4 years during 2015-03-31 thru 2019-09-30.
6. Some rates seem to have suspicious values at certain times:\
   Example 1: `Lebanon-Pound` has a fixed exchange rate of `1500.0` during 2008-09-30
   thru 2023-02-15, but then it changes to `15000.0` during 2023-02-15 thru 2023-12-31.
   (This does appear to be correct on research: a 90% devaluation in an 'official' rate).\
   Example 2: `ZIMBABWE-DOLLAR` on 2019-09-30 has an exchange rate of `0.0` (zero).\
   Example 3: `Zimbabwe-Dollar` on 2008-06-30 thru 2008-09-30 has an exchange rate of
   `8310000000.0` (8.31 billion).
7. The API itself seems to be case-sensitive.

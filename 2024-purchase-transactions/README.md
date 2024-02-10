# `purchase`

This application stores and retrieves purchase transactions, with currency conversion, as described in the
`WEX TAG and Gateways Product Brief.pdf` document.

When run, the application exposes two REST endpoints:

* `POST /purchase/` to store a new purchase transaction in US dollars.
* `GET /purchase/{id}?country_currency_desc={countryCurrencyDesc}` to retrieve a previously-stored purchase transaction,
  with an additional currency conversion from US dollars into the foreign currency.

## Implementation notes

This is a Spring Boot application built for Java 21, so a JRE 21 or JDK 21 will need to be installed in order to run or
build it respectively.

The repository includes the Maven Wrapper 3.2.0 to automatically download, install, cache and run Maven 3.9.5 when the
`mvnw` or `mvnw.cmd` command-line scripts are run, so it is not necessary to install Maven separately.

Database schema migration is done using Liquibase, so is database-independent. So the same code can run against both
the H2 embedded database (makes it easy to run the application standalone) or against an external PostgreSQL server
(more appropriate for a production instance).

The application state (other than caches) is only in the database, so the application can be scaled in production by
running multiple instances behind a load-balancer.

The currency exchange rates are provided by a `RatesOfExchangeClient` class that communicates with the online "Treasury
Reporting Rates of Exchange" dataset via the FiscalData Treasury.gov web API.

#### Caching of the web API

The default implementation of this (`CachedRatesOfExchangeClient` class) maintains a local in-memory cache of the
exchange rate data and only calls the web API once a day to check for updates (and only then if a request for data that
might have been updated comes in during that day).

There is also an initial embedded resource which is the entire dataset from 2001 through 2023 to initialize the cache.

If I were to spend more time on this, I'd consider storing the cache in the database and using a database-based
scheduler (for example, Quartz) to only download updates from the web API once per day even when horizontally-scaled.
The current implementation will download _n_ updates a day when horizontally-scaled to _n_ instances, i.e. each
instance maintains its own cache.

#### Justification for the caching strategy

Reading the
[Data Dictionary](https://fiscaldata.treasury.gov/datasets/treasury-reporting-rates-exchange/treasury-reporting-rates-of-exchange#dataset-properties),
the `effective_date` field is the correct one to use for conversion, not `record_date`.

A new record with a `record_date` corresponding to the end of the previous quarter, but a later `effective_date` can be
added to a quarter if the currency shifts by more than 10% within a quarter, and that new `exchange_rate` should be used
from the `effective_date` forward (until the next record).

The web API limits the response data size to 10,000 records in a single REST call, so we have to handle paging. Also,
data is only ever added to either the current quarter (same `record_date`, but new `effective_date`), or to the new
quarter when a new quarter begins.

This means the dataset is effectively "append-only", so updates only query forward from the last quarter's data forward.
Because of the way data is merged into the cache, if there are corrections to data in the last quarter they will be
updated, but older data is considered immutable.

There's actually less than 20,000 records in the entire dataset, so it can easily be cached in memory even if the
records were sized 1 kB each (they are probably just a few 100 bytes each, so the dataset will easily fit in less than
10 MB of memory).

So it is a valid caching strategy to have an embedded copy of most of the dataset and just check for new changes since
the last quarter or later once a day.

## Development vs. production usage

The requirements document requested a production-ready instance. Without using a docker-compose file (which would
lead to a dependency on a locally-installed Docker and Docker Compose as well as a JRE/JDK, and so break the standalone
requirement) I could not use a production-ready database. So instead, I've provided a default configuration that uses
the H2 embedded database, but which can easily be reconfigured to use PostgreSQL with a few configuration changes
(see [`application.yaml`](src/main/resources/application.yaml) and [`pom.xml`](pom.xml)). So the application is
"ready for production" with minimal effort.

For development, I included the Spring Boot DevTools, which enable certain useful but insecure features. They can be
turned off  by configuration (see [`application.yaml`](src/main/resources/application.yaml)) or even better by removing
`spring-boot-devtools` dependency altogether (see [`pom.xml`](pom.xml)).

The Swagger-UI and OpenAPI api-docs are exposed in the application because they are very useful in providing easy
access to try the REST endpoints (see [/swagger-ui.html](http://localhost:8080/swagger-ui.html)). They can be turned off
by configuration (see [`application.yaml`](src/main/resources/application.yaml)) or even better by removing
`springdoc-openapi-starter-webmvc-ui` dependency altogether (see [`pom.xml`](pom.xml)).

## Roadmap for future development

- [ ] Use `jakarta-validation` for the check on decimal places in the `#storeTransaction` controller method.
- [ ] Integration tests to cover more unhappy-paths, and odd cases in the exchange rate data.
- [ ] Enable monitoring with Prometheus or Graphite (or even just JMX).\
  &nbsp; (Note the `spring-boot-actuator-starter` is already enabled and provides some simple monitoring endpoints)
- [ ] Consider database-based caching to avoid horizontal-scaling increasing Treasury.gov web API usage.

## Anomalies in the Treasury.gov Rates of Exchange data

1. `country_currency_desc` is not consistent even for countries that have never  changed name nor currency.\
   Example: GBP is `UNITED KINGDOM-POUND STERLING` in 2015-03-31 through 2019-09-30, but `United Kingdom-Pound` before
   and after this period.
2. `country` is not consistent even if comparing names case-insensitively.\
   Example: it looks like `Antigua & Barbuda` should have been referred to as `ANTIGUA-BARBUDA` between 2015-03-31 and
   2019-09-30. However, some kind of parsing error has led to the `country` name being truncated to `ANTIGUA` (and
   `BARBUDA-` has been prefixed erroneously to the `currency` name in this same period).
3. Some countries change `currency` over time (the actual currency changes, not just the name used in the data) and may
   have multiple currencies over a period.\
   Example 1: `Germany` in 2001-03-31 through 2002-03-31 (and 2004-09-30 through 2008-03-31) has `Euro` and `Mark`
   currencies.\
   Example 2: `Venezuela` has `Bolivar Soberano` and `Fuerte (OLD)` in 2019-12-31 through 2023-12-31; `BOLIVAR-FUERTE`
   and `BOLIVAR-SOBERANO` in 2018-09-30 through 2019-09-30; `BOLIVAR` in 2015-03-31 through 2018-06-30; `Soberano (OLD)`
   in 2001-03-31 through 2014-12-31.
4. Currency names may or may not uniquely identify a currency without qualifying the currency with the country.\
   Example 1: Many countries use the `Dollar`, `Peso`, `Pound` or `Shilling` but these are not the same currencies.\
   Example 2: Many countries use the `Euro` or `E. Caribbean Dollar` and these _are_ in fact the same currencies.
5. There are some unexplained large gaps in the data.\
   Example: No rates for `St. Lucia` for over 4 years during 2015-03-31 through 2019-09-30.
6. Some rates seem to have suspicious values at certain times:\
   Example 1: `Lebanon-Pound` has a fixed exchange rate of `1500.0` during 2008-09-30 through 2023-02-15, but then it
   changes to `15000.0` during 2023-02-15 through 2023-12-31. (This does appear to be correct on research: a 90%
   devaluation in Lebanon's "official" exchange rate occurred in February 2023).\
   Example 2: `ZIMBABWE-DOLLAR` on 2019-09-30 has an exchange rate of `0.0` (zero).\
   Example 3: `Zimbabwe-Dollar` on 2008-06-30 through 2008-09-30 has an exchange rate of `8310000000.0` (8.31 billion).
7. The API itself seems to be case-sensitive.

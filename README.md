# Assignments

Some coding exercise assignments:

- [`2011-bliffoscope`](2011-bliffoscope)
- [`2011-triangle-min-path`](2011-triangle-min-path)
- [`2023-mockito-cars`](2023-mockito-cars)
- [`2024-purchase-transactions`](2024-purchase-transactions)

## Build

Each subdirectory is a Gradle subproject of this repo's single multi-project build, sharing the
root Gradle wrapper (`./gradlew`) and a Java 25 toolchain. Code is formatted with
[Spotless](https://github.com/diffplug/spotless) (Google Java Format). CI runs
`./gradlew spotlessCheck build` on every push and pull request via GitHub Actions.

# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Jackson datatype module for serializing/deserializing [Joda-Time](https://www.joda.org/joda-time/) types (`DateTime`, `LocalDate`, `Duration`, `Period`, `Interval`, etc.).

## Branches: 2.x vs 3.x

This repo has two maintained lines with different packages and APIs. **Check the current branch before editing.**

| | 2.x line (`2.x`, `2.NN` branches) | 3.x line (`3.x`/`3.NN` branches) |
|---|---|---|
| Group / package | `com.fasterxml.jackson.datatype` / `com.fasterxml.jackson.datatype.joda` | `tools.jackson` / `tools.jackson.datatype.joda` |
| Java baseline | Java 8 (CI: 8, 17, 21) | Java 17 (CI: 17, 21, 25) |
| `JodaModule` | extends `SimpleModule` | extends `JacksonModule` |
| JPMS descriptor | `src/moditect/module-info.java` (added by Moditect plugin) | `src/main/java/module-info.java` (+ `src/test/java/module-info.java`) |
| SPI file | `META-INF/services/com.fasterxml.jackson.databind.Module` | `META-INF/services/tools.jackson.databind.JacksonModule` |
| Extra | `JodaMapper` convenience `ObjectMapper` subclass | — |

Fixes typically land on the oldest affected branch and are merged forward (e.g. `2.22` → `2.x`). Release notes are in `release-notes/VERSION-2.x` (or `VERSION` on 3.x).

## Build Commands

```bash
# Build without tests
./mvnw -B -q -ff -ntp -DskipTests clean package

# Full build + tests (what CI runs)
./mvnw -B -ff -ntp clean verify

# Run a single test class / method
./mvnw test -Dtest=DateTimeTest
./mvnw test -Dtest=DateTimeTest#testDateTimeDeserFromString

# Test against a specific Joda-Time version (CI matrix uses JODA_MIN / JODA_DEFAULT / JODA_MAX
# from .github/workflows/main.yml; on 2.x min is 2.9)
./mvnw -B -q -ff -ntp -DskipTests clean package
./mvnw -B -q -ff -ntp -Dversion.joda=2.9 surefire:test
```

The default Joda version is the `version.joda` property in `pom.xml`. Code must stay compatible with the minimum Joda version tested in CI.

## Architecture

Package root: `<pkg>.datatype.joda` (see table above).

- **`JodaModule`** — entry point; registers all serializers, deserializers, and key deserializers.
- **`ser/`** — `JodaSerializerBase` → `JodaDateSerializerBase` → concrete (e.g. `DateTimeSerializer`), plus simple period-type serializers (`DaysSerializer`, `HoursSerializer`, ...).
- **`deser/`** — `JodaDeserializerBase` → `JodaDateDeserializerBase` → concrete. `deser/key/` has Map key deserializers.
- **`cfg/`** — `JacksonJodaDateFormat` (wraps `DateTimeFormatter`) and `JacksonJodaPeriodFormat` (wraps `PeriodFormatter`), sharing `JacksonJodaFormatBase` (locale/timezone handling). `FormatConfig` holds static default formats. Format objects are immutable; `withXxx()` returns new instances.

### Key Patterns

- Serializers/deserializers are **contextual** (`createContextual()`): `@JsonFormat` overrides pattern, shape, timezone, and locale by deriving a new format and a new (de)serializer instance.
- Date/time output shape is chosen by `@JsonFormat(shape=...)` and `SerializationFeature.WRITE_DATES_AS_TIMESTAMPS`: `STRING` (ISO/pattern), `NUMBER_INT` (epoch millis), or `ARRAY` (component array for partials like `LocalDate`).
- `PackageVersion.java` is generated from `PackageVersion.java.in` by the `replacer` plugin at build time — edit the `.in` template, not the generated file.

## Testing

JUnit 5. `JodaTestBase` provides mapper builder helpers and utilities. Tests are grouped under `ser/`, `deser/`, `depr/` (deprecated types such as `DateMidnight`), plus top-level annotation/SPI tests (`ModuleSPIMetadataTest` checks the SPI/module metadata).

Known-failing tests (reproductions of open issues) use `@JacksonTestFailureExpected` from `testutil/failure`: the test passes while the underlying bug exists and fails once it is fixed, at which point the annotation should be removed.

## Parent POM

Inherits from `jackson-base` (`com.fasterxml.jackson` on 2.x, `tools.jackson` on 3.x), which provides most plugin configuration (surefire, javadoc, OSGi bundle, Moditect, release).

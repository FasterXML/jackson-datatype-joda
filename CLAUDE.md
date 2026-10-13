# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Jackson datatype module for serializing/deserializing [Joda-Time](https://www.joda.org/joda-time/) types (`DateTime`, `LocalDate`, `Duration`, `Period`, `Interval`, etc.).

This file describes the **3.x line** (branches `3.0`, `3.1`, `3.2`, `3.x`): coordinates `tools.jackson.datatype:jackson-datatype-joda`, package `tools.jackson.datatype.joda`, Java 17 baseline, JPMS via `src/main/java/module-info.java`. Annotations still come from `com.fasterxml.jackson.annotation` (they were not renamed in 3.x).

The 2.x line (`2.NN`, `2.x` branches) differs substantially: package `com.fasterxml.jackson.datatype.joda`, Java 8, `JodaModule extends SimpleModule`, `module-info` in `src/moditect/`, SPI file `META-INF/services/com.fasterxml.jackson.databind.Module`, plus `JodaMapper` and deprecated `DateMidnight` support (both removed in 3.x). **Check the current branch before editing.** Fixes are merged forward (e.g. `3.1` → `3.2` → `3.x`); release notes go in `release-notes/VERSION` (3.x) or `VERSION-2.x` (2.x).

## Build Commands

```bash
# Build without tests
./mvnw -B -q -ff -ntp -DskipTests clean package

# Full build + tests (what CI runs)
./mvnw -B -ff -ntp clean verify

# Run a single test class / method
./mvnw test -Dtest=DateTimeTest
./mvnw test -Dtest=DateTimeTest#testDateTimeDeserFromString

# Test against a specific Joda-Time version (build first, then run surefire only)
./mvnw -B -q -ff -ntp -DskipTests clean package
./mvnw -B -q -ff -ntp -Dversion.joda=2.10 surefire:test
```

CI (`.github/workflows/main.yml`) runs Java 17/21/25 against Joda 2.12.7 (default, `version.joda` in `pom.xml`), 2.10 (minimum), and 2.13.0 (maximum). Code must stay compatible with Joda 2.10.

## Architecture

- **`JodaModule`** — entry point; extends `JacksonModule` and registers all serializers, deserializers, and key deserializers in `setupModule()`. Discoverable via `ServiceLoader` (`META-INF/services/tools.jackson.databind.JacksonModule` and `provides` in `module-info.java`).
- **`ser/`** — `JodaSerializerBase` → `JodaDateSerializerBase` → concrete (e.g. `DateTimeSerializer`), plus simple period-type serializers (`DaysSerializer`, `HoursSerializer`, ...).
- **`deser/`** — `JodaDeserializerBase` → `JodaDateDeserializerBase` → concrete. `deser/key/` has Map key deserializers.
- **`cfg/`** — `JacksonJodaDateFormat` (wraps `DateTimeFormatter`) and `JacksonJodaPeriodFormat` (wraps `PeriodFormatter`), sharing `JacksonJodaFormatBase` (locale/timezone handling). `FormatConfig` holds static default formats. Format objects are immutable; `withXxx()` returns new instances.

### Key Patterns

- Serializers/deserializers are **contextual** (`createContextual()`): `@JsonFormat` overrides pattern, shape, timezone, and locale by deriving a new format and a new (de)serializer instance.
- Date/time output shape is chosen by `@JsonFormat(shape=...)` and the timestamps-vs-text date feature: `STRING` (ISO/pattern), `NUMBER_INT` (epoch millis), or `ARRAY` (component array for partials like `LocalDate`).
- `PackageVersion.java` is generated from `PackageVersion.java.in` by the `replacer` plugin at build time; edit the `.in` template, not the generated file.

## Testing

JUnit 5. `JodaTestBase` provides mapper builder helpers and utilities. Tests are grouped under `ser/` and `deser/`, plus top-level tests (annotations, `ModuleSPIMetadataTest` for SPI/module metadata, `JDKSerializabilityTest`, `TestVersions`).

Tests run on the module path using `src/test/java/module-info.java`. **A new test package must be added there as an `opens` entry**, or JUnit can't access it.

Known-failing tests (reproductions of open issues) use `@JacksonTestFailureExpected` from `testutil/failure`: the test passes while the bug exists and fails once it is fixed, at which point the annotation should be removed.

## Parent POM

Inherits from `tools.jackson:jackson-base`, which provides most plugin configuration (surefire, javadoc, OSGi bundle, release). Other workflows (`dep_build_v2.yml`, `dep_build_v3.yml`) re-run builds when jackson-databind is pushed.

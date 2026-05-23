## [2.0.0]

### Added

- Generic numeric type support via [`KevalNumber<N>`](src/commonMain/kotlin/com/notkamui/keval/KevalNumber.kt): parsing rules and default resources are defined per number type.
- [`KevalNumbers.Double`](src/commonMain/kotlin/com/notkamui/keval/KevalNumberDouble.kt) — full default operator/function/constant set (same behaviour as v1.x).
- [`KevalNumbers.BigDecimal`](src/jvmMain/kotlin/com/notkamui/keval/KevalNumberBigDecimal.kt) — JVM-only built-in for `java.math.BigDecimal` with arithmetic, comparison, aggregates, and rounding defaults (no trig/log/random).
- [`String.kevalBigDecimal()`](src/jvmMain/kotlin/com/notkamui/keval/KevalNumberBigDecimal.kt) and `KevalNumber<N>.eval(String)` convenience entry points.
- Scientific notation in numeric literals (e.g. `1e10`, `1.5e-3`).
- Negative integer exponents for BigDecimal `^` (e.g. `2 ^ -2` → `0.25`).
- Extensive JVM test suite for BigDecimal evaluation, parsing, builder API, and error cases.

### Changed

#### Non-breaking

- `String.keval()` and `Keval.eval(String)` remain `Double`-only shortcuts with the same ergonomics as v1.x.
- Android, JS, and Native targets continue to use the `Double` API from `commonMain` without an explicit Android publication target.

#### Breaking

- `Keval` is now `Keval<N>` and requires a [`KevalNumber<N>`](src/commonMain/kotlin/com/notkamui/keval/KevalNumber.kt) context.
- `Keval.create { … }` → `Keval.create(KevalNumbers.Double) { … }`.
- Operator implementations: `(Double, Double) -> Double` → `(N, N) -> N`; `(Double) -> Double` → `(N) -> N`.
- Function implementations: `(DoubleArray) -> Double` → `(List<N>) -> N`.
- `KevalBuilder.DEFAULT_RESOURCES` removed; use `KevalNumbers.Double.defaultResources()`.
- `KevalBuilder` constructor is internal; build instances through `Keval.create(number) { … }`.

### Fixed

## [1.2.0]

### Added

### Changed

#### Non-breaking

#### Breaking

### Fixed

- Publishing

## [1.1.1]

### Added

### Changed

#### Non-breaking

#### Breaking

### Fixed

- Constant name tokenization used to fail for names with multiple digits and underscores, doesn't anymore ([#49](https://github.com/notKamui/Keval/pull/49) by [@smelfungus](https://github.com/smelfungus))

## [1.1.0]

### Added

- Introduced changelog file (for previous changelogs, check the [Releases page](https://github.com/notKamui/Keval/releases)

### Changed

#### Non-breaking

- Functions now supports flexible arity ([#46](https://github.com/notKamui/Keval/pull/46) by [@jindraregal](https://github.com/jindraregal))

#### Breaking

### Fixed

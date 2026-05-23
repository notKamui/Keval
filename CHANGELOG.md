## [2.0.0]

### Added

- Generic numeric type support via [`KevalNumber<N>`](src/commonMain/kotlin/com/notkamui/keval/KevalNumber.kt): parsing rules and default resources are defined per number type.
- Split typeclass interfaces: [`KevalLiteralParser<N>`](src/commonMain/kotlin/com/notkamui/keval/KevalNumber.kt), [`KevalDefaults<N>`](src/commonMain/kotlin/com/notkamui/keval/KevalNumber.kt), and [`KevalNumber<N>`](src/commonMain/kotlin/com/notkamui/keval/KevalNumber.kt) (includes non-overridable implicit multiplication via `multiply()`).
- [`KevalNumbers.real`](src/commonMain/kotlin/com/notkamui/keval/KevalNumber.kt) — primary name for the built-in `Double` implementation (full default operator/function/constant set, same behaviour as v1.x).
- [`KevalNumbers.BigDecimal`](src/jvmMain/kotlin/com/notkamui/keval/KevalNumberBigDecimal.kt) — JVM-only built-in for `java.math.BigDecimal` with arithmetic, comparison, aggregates, and rounding defaults (no trig/log/random). Configurable precision via [`KevalNumberBigDecimal.withContext(MathContext)`](src/jvmMain/kotlin/com/notkamui/keval/KevalNumberBigDecimal.kt).
- [`CompiledExpression<N>`](src/commonMain/kotlin/com/notkamui/keval/CompiledExpression.kt) and [`Keval.compile()`](src/commonMain/kotlin/com/notkamui/keval/Keval.kt) — parse once, evaluate many times.
- **Variables**: identifiers that are not operators, functions, or constants; [`eval(expression, bindings)`](src/commonMain/kotlin/com/notkamui/keval/Keval.kt), [`KevalUnresolvedVariableException`](src/commonMain/kotlin/com/notkamui/keval/KevalException.kt), implicit multiplication with variables (`x(y+1)`, `2 x`).
- Unified entry points: [`String.evalWith()`](src/commonMain/kotlin/com/notkamui/keval/KevalNumber.kt), [`String.compileWith()`](src/commonMain/kotlin/com/notkamui/keval/KevalNumber.kt), [`KevalNumber.eval()`](src/commonMain/kotlin/com/notkamui/keval/KevalNumber.kt).
- Non-throwing API: [`evalOrNull`](src/commonMain/kotlin/com/notkamui/keval/Keval.kt) / [`evalResult`](src/commonMain/kotlin/com/notkamui/keval/Keval.kt) on `Keval` and `CompiledExpression`; `String.kevalOrNull()` / `String.kevalResult()` for `Double`.
- [`String.kevalBigDecimal()`](src/jvmMain/kotlin/com/notkamui/keval/KevalNumberBigDecimal.kt) JVM convenience entry point.
- Scientific notation in numeric literals (e.g. `1e10`, `1.5e-3`).
- Negative integer exponents for BigDecimal `^` (e.g. `2 ^ -2` → `0.25`).
- Shared [`BooleanLogic`](src/commonMain/kotlin/com/notkamui/keval/BooleanLogic.kt) defaults for Double and BigDecimal.
- Fixed-arity function AST nodes (`Function1Node`–`Function4Node`) to reduce allocations during parsing.
- Extensive test suite for variables, compiled expressions, BigDecimal evaluation, parsing, builder API, and error cases.

### Changed

#### Non-breaking

- `String.keval()` and `Keval.eval(String)` remain `Double`-only shortcuts with the same ergonomics as v1.x.
- Android, JS, and Native targets continue to use the `Double` API from `commonMain` without an explicit Android publication target.
- Implicit multiplication behaviour is unchanged: `(2+3)(4+6)`, `3(2+2)`, `1 2` still work; `*` cannot be overridden by consumers.

#### Breaking

- `Keval` is now `Keval<N>` and requires a [`KevalNumber<N>`](src/commonMain/kotlin/com/notkamui/keval/KevalNumber.kt) context.
- `Keval.create { … }` → `Keval.create(KevalNumbers.real) { … }`.
- `KevalNumbers.Double` removed; use `KevalNumbers.real`.
- Operator implementations: `(Double, Double) -> Double` → `(N, N) -> N`; `(Double) -> Double` → `(N) -> N`.
- Function implementations: `(DoubleArray) -> Double` → `(List<N>) -> N`.
- `KevalBuilder.DEFAULT_RESOURCES` removed; use `KevalNumbers.real.defaultResources()`.
- `KevalBuilder` constructor is internal; build instances through `Keval.create(number) { … }`.
- `KevalNumberBigDecimal` is now a class (use `KevalNumberBigDecimal.Default` or `KevalNumbers.BigDecimal`) instead of an `object`.
- `KevalInvalidExpressionException` is sealed; direct instantiation replaced by subtypes such as `KevalInvalidSymbolException`.

### Fixed

- `randRange` now correctly rejects non-positive step values.
- BigDecimal `ne` aligned with `eq`: uses `compareTo` for scale-independent numeric equality.

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

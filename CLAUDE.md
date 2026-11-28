# CLAUDE.md

Symja is a Java computer algebra system that evaluates Mathematica-style input. This file is
guidance for working in this repository.

## Layout

- The git root has no `pom.xml`. The Maven reactor root is `symja_android_library/` (one level
  down). Run every `mvn` command from there: a `mvn -pl ...` started in the git root fails at
  once, and a piped `grep` can hide that. Check for `BUILD SUCCESS`.
- Java 21 (`maven.compiler.release` in the parent POM).
- Main modules, in dependency order:
  - `matheclipse-external`: vendored and adapted libraries (apfloat algorithms, Hipparchus glue, ...)
  - `matheclipse-parser`: scanner and parser
  - `matheclipse-core`: the engine
  - feature modules: `-image`, `-graphtheory`, `-chem`, `-bio`, `-astro`, `-nlp`, `-dataset`,
    `-or-tools`, `-compile`, `-jsgraphics`, `-internal`, `-gpl`
  - `matheclipse-io`: import/export, the servlet, the ArchUnit rules and many integration tests
  - `matheclipse-api`, `-web`, `-script`, `-discord`: front ends
  - `tools`: generators such as `RulePreprocessor`
- Inside `matheclipse-core/src/main/java/org/matheclipse/core/`:
  - `expression/`:
    - `F` holds the factory methods: `F.Plus(...)`, `F.QQ(n, d)` for fractions, `F.NIL`.
    - `S` holds the built-in symbols.
    - `ID` holds the integer symbol ids.
  - `builtin/`: most built-in functions, grouped in classes such as `Arithmetic` or
    `ListFunctions`. Each has an `Initializer` that calls `S.Name.setEvaluator(...)`.
  - `reflection/system/`: one class per larger built-in, such as `Integrate`, `Limit` or `Solve`.
  - `reflection/system/rules/`: rule classes generated from `rules/*.m`; don't edit them by hand.
  - `eval/`:
    - `EvalEngine` is the evaluator.
    - `Errors` holds the message templates.
    - `TimeLimits` runs a step under a time limit.
  - `integrate/rubi/`: the Rubi integration rules.
- `symja_android_library/rules/*.m`: pattern rules in Symja syntax. After editing one, regenerate
  its Java class with `tools/.../preprocessor/RulePreprocessor`.
- `symja_android_library/doc/functions/<Name>.md`: one documentation page per function.

## Build and test

Run from `symja_android_library/`. Add `-o` when the dependencies are already in `~/.m2`.

```
mvn -o -q compile -pl matheclipse-external,matheclipse-parser,matheclipse-core
mvn -o test -pl matheclipse-core -Dtest='LowercaseTestCase,SolveTest'   # classes separated by commas
mvn -o test -pl matheclipse-core -Dtest='LowercaseTestCase#testFindClusters'
mvn verify                       # fast tier, what CI runs
mvn verify -Pall-tests           # fast + slow, run before pushing
mvn -pl matheclipse-io -am test -Dtest='*ArchTest'   # ArchUnit rules (slow-tagged)
```

- **Test tiers:** CONTRIBUTING.md describes them.
  - A test method that takes more than about a second gets `@Tag(TestTags.SLOW)`; in other modules
    write `@Tag("slow")`.
  - The PR build's `.github/scripts/check-test-budget.py` fails over a slow method that is not tagged.
- **`-Dtest` traps:**
  - `-Dtest=A+B` runs nothing: `+` joins methods, so separate classes with commas.
  - `-pl X` without `-am` resolves the upstream modules from `~/.m2`, which may hold an old install.
  - `-rf :module` does the same.
- **Engine tests:**
  - Extend `ExprEvaluatorTestCase` and use `check(input, expectedOutput)`.
  - Input is in *relaxed* syntax: `f(x)` instead of `f[x]`, built-in names are case-insensitive,
    and unknown heads are lowercased.
  - Write a curried call as `Derivative(1)[f][x]`. `Derivative(1)(f)(x)` parses as a product.
  - Expected strings are Symja's `OutputForm`.
  - The test engine limits recursion and iteration, and caps expression size at 20000 and integer
    bit length at 200000. Keep test inputs below these limits.
  - Write your own test inputs, not examples copied from external documentation, excluding open sources like Wikipedia, OEIS, ...

## Code conventions

- **`F.NIL`:** a method that can return `F.NIL` (meaning "no result, leave unevaluated") must be
  annotated with `@AllowNIL` (`org.matheclipse.core.annotation`). The ArchUnit rules in
  `matheclipse-io` (`ReturnNilAnnotationArchTest`, `FNilContractArchTest`) check this.
- Use `F.QQ(numerator, denominator)` for rational numbers.
- **Long-running code:**
  - Long loops call `engine.checkTimeLimit()`.
  - A step that may take long or not terminate runs under
    `TimeLimits.callOrNil` / `TimeLimits.evaluateOrNil`.
- **Messages:** new templates go into `eval/Errors.java`. Emit them with `Errors.printMessage(...)`;
  the owning symbol is the head of the message.
- **Root causes:** fix the root cause in the general code path. Don't special-case one input.
  `Reduce` must not call `Solve`.
- **Source comments:** describe Symja's behaviour, not another system's
  ("as Mathematica does", "WMA returns ...").
- **New built-in symbols:**
  - A new symbol changes `ID.java` (ids are array indices, renumbered on insertion), `S.java` and
    `convert/AST2Expr.java`.
  - These tables are generated; don't add symbols without coordinating with the maintainer.
  - After a table change, rebuild the whole reactor with `clean`. `ID` constants are inlined by
    javac, so a stale module resolves names to the wrong symbols without an error.
  - `SymbolTableTest` checks the tables.

## Traps

- **Stale classes:**
  - Incremental compilation does not rebuild a class whose superclass changed, and can leave out
    anonymous classes (`X$1`).
  - A run with many `NoClassDefFoundError` *errors* and 0 failures means stale classes, not a
    regression. Rerun with `clean`.
  - An IDE building into the same `target/classes` can cause the same errors.
- Don't edit sources or start another `mvn` in a module while a test run uses that module's classes.
- **Symja's own output form:** some external test corpora print values the way Mathematica displays
  them, not in Symja's `OutputForm`. Check the actual value before "fixing" an expected string.

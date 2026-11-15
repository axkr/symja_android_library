# Parser golden corpus

A behavioural snapshot of `ExprParser`, used to make parser refactorings verifiable.

`ParserGoldenTest` parses every corpus entry with both parsers, in four parser modes, and compares
the result against the recorded golden file. A refactoring that is meant to preserve behaviour has
to leave all twenty-four golden files byte-identical; a change that is meant to alter a parse has
to show exactly which inputs it altered.

Both parsers are recorded because they share `Scanner` but not the parsing: `ExprParser` builds
`IExpr` and rewrites on the way (`Sqrt(x)` becomes `Power(x, 1/2)`), while `Parser` builds a plain
`ASTNode` tree with none of that. A change to the shared scanner can break one and not the other.
A third parser, `FuzzyParser` in matheclipse-api, also extends the same scanner but is **not**
covered here, because matheclipse-api sits downstream of this module — changes to `Scanner` should
still be checked against it by hand.

The recording is taken at **parse** level, before evaluation. Evaluation hides most of what a
parser change can break — `2*x` and `x*2` evaluate alike, the associativity of a flat operator
stops being observable once arguments are sorted, and a wrong grouping between two operators of
equal precedence often still yields the same answer. The existing end-to-end tests cover
evaluation; these files cover the shape of the tree the parser hands over.

## Files

| File | Contents |
| --- | --- |
| `operators.txt` | *generated* — every operator token in the live table put through 18 syntactic shapes, plus a pairwise precedence matrix over the common infix operators |
| `doc.txt` | *generated* — the `>>` example inputs harvested from `doc/functions/*.md` |
| `edge-cases.txt` | **hand-written** — number literals, patterns, slots, spans, parts, contexts, comments, line structure, syntax errors |
| `golden/<corpus>.<parser>.<mode>.txt` | *generated* — one `input<TAB>result` line per corpus entry |

Parsers are `expr` (`ExprParser`, recorded as `IExpr.fullFormString()`) and `ast` (`Parser`,
recorded as `ASTNode.toString()`).

Modes are `relaxed` (the console default), `mma` (`f[x]` bracket syntax, case-sensitive),
`explicit-times` (`*` required, E-notation and prefixed integer literals enabled) and
`dominant-times` (implicit multiplication binds tightest).

A golden result is either the full form of the parsed expression, or a `!`-prefixed marker:
`!syntax@<column>: <error>`, `!StackOverflowError`, or `!<ExceptionClass>: <message>`. **Failures
are part of the contract** — a syntax error that stops being raised is a grammar change.

## Encoding

One entry per line. `\\` is a backslash, `\n` a newline, `\r` a carriage return, `\t` a tab.
Lines starting with `#|` are comments and blank lines are ignored — `#` itself cannot introduce a
comment, because `#`, `#1`, `##` and `# + 1` are all corpus entries in their own right.

## Adding a case

Append it to `edge-cases.txt`, then regenerate. Do not hand-edit the golden files or the two
generated corpora.

## Regenerating

```bash
mvn -pl matheclipse-core test-compile
mvn -pl matheclipse-core dependency:build-classpath -Dmdep.outputFile=/tmp/cp.txt -Dmdep.includeScope=test
java -cp "matheclipse-core/target/classes:matheclipse-core/target/test-classes:matheclipse-parser/target/classes:$(cat /tmp/cp.txt)" \
  org.matheclipse.core.parser.golden.ParserCorpusGenerator matheclipse-core
```

**`test-compile` first, always.** The generator reads the corpus through the classloader, so it
sees `target/test-classes`, not `src/test/resources`. Editing a corpus and regenerating without
recompiling rewrites the golden files from the *previous* corpus, reports success, and silently
drops what you just added. The generator now refuses to run in that state, but the command above
includes `test-compile` for a reason.

Regenerate only after reading the diff and confirming every changed line is intended. Regenerating
to make a red test go green discards the only thing these files are for.

A "no diff" result is only meaningful once you have confirmed the run actually happened: check that
the compile succeeded, that the generator printed its entry counts, and that the counts match what
you expect. Three times during this work a green-looking check turned out to be measuring nothing.

## Benchmark

`ParserBenchmark` measures throughput over the same corpora. Pass workload names to run a subset —
do that when comparing two builds, because all workloads JIT-compile the same parser methods and
whichever runs first shapes the profile the rest inherit:

```bash
java -cp "<same classpath>" org.matheclipse.core.parser.golden.ParserBenchmark operators
```

It is a plain timing harness, not JMH: good enough to size a change of ten percent or more, not
enough to defend a claim of a few percent.

# Changelog

Noteworthy changes are documented in this file.


## [Unreleased]

- Java 11 required
- first Maven Central release (contributed by [@HannesWell](https://github.com/HannesWell))
- Maven modules matheclipse-parser, matheclipse-logging, matheclipse-core are LGPL licensed
- Maven modules matheclipse-gpl and dependents are GPL licensed
- Symja script engine moved to Maven module matheclipse-script 
- unified/refactored logging - moved basics into new matheclipse-logging Maven module (contributed by [@HannesWell](https://github.com/HannesWell))
- new matheclipse-jar Maven module to create docker container: https://hub.docker.com/r/symja/symja-2.0
- new matheclipse-discord Maven module for a discord bot based on `Discord4J`
- improved graphical output in browser apps: https://github.com/axkr/symja_android_library/wiki/Browser-apps
- browser app Javascript graphics now contains a `jsfiddle` button to analyze the generated `jsxgraph`, `mathcell` and `plotly` iframe sources.
- improved JSON API: https://github.com/axkr/symja_android_library/wiki/API
- pattern-matching made more compatible with Mathematica pattern-matching 
- improved rationalization of Java `double` numbers in `Rationalize`
- improved `FindMinimum, FindMaximum, FindRoot`
- new `IAST` interface implementation `ASTRRBTree` uses "structural sharing" similar like collections in Scala or Clojure for example for improved `Expand` performance for very large expressions.
- new functions:  `BioSequence, BioSequenceQ`
- new functions: `ApplySides, AddSides, DivideSides, MultiplySides, SubtractSides` 
- improvements for expressions objects: `Association, Dataset, Graph, Quantity, SparseArray`
- improvements in `PiecewiseExpand, PossibleZeroQ, Solve, Eliminate, FunctionExpand, FullSimplify` functions
- new `CarlsonRD, CarlsonRF, CarlsonRG, CarlsonRJ,...` functions
- uses mathics-threejs-backend for `Graphics3D, ListLinePlot3D, ListPointPlot3D, ListPlot3D` functions: https://github.com/Mathics3/mathics-threejs-backend  (contributed by [@TiagoCavalcante](https://github.com/TiagoCavalcante))
- improved JSON for `ImportString, ExportString`
- improved String Regex functions for example in `StringSplit, StringReplace` functions
- uses Janino compiler for `Compile` function: https://github.com/janino-compiler/janino; improved `CompilePrint` function


### Contributers
- [@axkr](https://github.com/axkr)
- [@HannesWell](https://github.com/HannesWell)
- [@shaunlebron](https://github.com/shaunlebron)
- [@TiagoCavalcante](https://github.com/TiagoCavalcante)
- [@tranleduy2000](https://github.com/tranleduy2000)
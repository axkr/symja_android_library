# Changelog

Noteworthy changes are documented in this file.

## [Unreleased](https://github.com/axkr/symja_android_library/compare/v3.0.0...HEAD)
- Java 11 required
- Maven modules matheclipse-parser, matheclipse-logging, matheclipse-core are LGPL licensed
- Maven modules matheclipse-gpl and dependents are GPL licensed
- function documentation: [symja_android_library/doc/function](https://github.com/axkr/symja_android_library/tree/master/symja_android_library/doc/functions)

- migrate JUnit 3 to JUnit 4 (#861)
- using apfloat 1.13.0, 1.14.0 [Changelog](http://www.apfloat.org/apfloat_java/history.html)
- improved `Position` (#859)
- improved `FullSimplify, Simplify, Together` (#856)
- improved `LeviCivitaTensor, Hypergeometric0F1Regularized, Hypergeometric1F1Regularized, Hypergeometric2F1Regularized`
- Fix bug in `ComplexSym#powPositive)` for n==0
- fix some bugs in [Limit](https://github.com/axkr/symja_android_library/tree/master/symja_android_library/doc/functions/Limit.md)
- fix bugs in [Minors](https://github.com/axkr/symja_android_library/tree/master/symja_android_library/doc/functions/Minors.md) (#766)
- improve `Definition` add [FullDefinition](https://github.com/axkr/symja_android_library/tree/master/symja_android_library/doc/functions/FullDefinition.md) [Save](https://github.com/axkr/symja_android_library/tree/master/symja_android_library/doc/functions/Save.md) function  (#972)
- add `SameTest` option for `FixedPoint*, Contains*` and set functions `Union, Intersectioin, Complement`
- very basic Matlab read file support(#982)
- [FindRoot](https://github.com/axkr/symja_android_library/tree/master/symja_android_library/doc/functions/FindRoot.md): use Newton method as default instead of Brent (#974)
- Search keywords in web interface without * at the end (#971)
- [FindMinimum](https://github.com/axkr/symja_android_library/tree/master/symja_android_library/doc/functions/FindMinimum.md), FindMaximum add method "SequentialQuadratic" 87857a8d29b721cb9879c7188f46f0071e9962b3
- improve, fix bugs in [Merge](https://github.com/axkr/symja_android_library/tree/master/symja_android_library/doc/functions/Merge.md)
- added [SubsetCases](https://github.com/axkr/symja_android_library/tree/master/symja_android_library/doc/functions/SubsetCases.md), [SubsetReplace](https://github.com/axkr/symja_android_library/tree/master/symja_android_library/doc/functions/SubsetReplace.md)
- improved [N](https://github.com/axkr/symja_android_library/tree/master/symja_android_library/doc/functions/N.md) evaluation (#937, #942)
- improved [TrigExpand](https://github.com/axkr/symja_android_library/tree/master/symja_android_library/doc/functions/TrigExpand.md) (#930)
- use rational gcd and lcm in [PolynomialGCD](https://github.com/axkr/symja_android_library/tree/master/symja_android_library/doc/functions/PolynomialGCD.md), [PolynomialLCM](https://github.com/axkr/symja_android_library/tree/master/symja_android_library/doc/functions/PolynomialLCM.md)
 
## [v3.0.0](https://github.com/axkr/symja_android_library/compare/v3.0.0) - 2023-11-11
- Java 11 required
- Maven modules matheclipse-parser, matheclipse-logging, matheclipse-core are LGPL licensed
- Maven modules matheclipse-gpl and dependents are GPL licensed
- function documentation: [symja_android_library/doc/function](https://github.com/axkr/symja_android_library/tree/master/symja_android_library/doc/functions)


- new function `DedekindNumber` for first 0..9 Dedekind numbers
- Renamed ISignedNumber (Symja 2.x) interface to IReal (Symja 3.x) interface
- Renamed methods `evalComplex->evalfc` and `evalDouble->evalf`
- improved NIntegrate with `GaussKronrod` method (Maven dependency `de.labathome` `AdaptiveQuadrature`
- `Hypergeometric2F1` uses apfloat algorithm for `double` and `Complex` values
- `EvalEngine#evalDouble()` returns `POSITIVE_INFINITY, NEGATIVE_INFINITY` for `Infinity, -Infinity`
- use eclipse-temurin:21_35-jre in JIB Docker script 
- improve `PolynomialHomogenization` with a Cos/Sin transform to find more solutions
- implement SawtoothWave (#783)
- improved `Arg, ApplySides, Association, Assumptions, Bessel..., Binomial, BooleanFunction, Cancel, Carlson..., Catalan, CatalanNumber, CholeskyDecomposition, Chop, Complement, ComplexExpand, Count, CorrelationDistance, CosineDistance, D, Derivative, Drop, EigenValues, Eigenvectors, EllipticF, EllipticPi, FactorTerms, FindLinearRecurrence, Function, FunctionExpand, FullSimplify, GCD, Glaisher,Grad, HankelH1, HankelH2, HarmonicNumber, HermiteMatrix, HurwitzZeta, Hypergeometric..., Identity, IdentityMatrix, Import, ImportString, IntegerDigits, Intersection, Interval..., Khinchin, Limit, LinearRecurrence, ListConvolve, ListCorrelate, LogisticSigmoid, MantissaExponent, MapIndexed, MathMLForm, Max, Min, Minors, NMinimize, NestList, NestWhile, NestWhileList, NMaximize, NSolve, OrderedQ, Orthogonalize, PiecewiseExpand, PolyGamma, PowerExpand, PossibleZeroQ, Product, Projection, Quantity, RandomVariate, Range, ReleaseHold, Round, SatisfiabilityInstances, Simplify, Sign, Solve, SphericalHankelH1, SphericalHankelH2, StieltjesGamma, StringSplit, Subfactorial, Sum, Surd, TagSet, TagSetDelayed, Take, TakeLargestBy, TakeSmallestBy, TeXForm, TimeConstrained, Together, Unitize, Union, Zeta` function
- new functions: `Adjugate, DeleteMissing, Eigensystem, FromSphericalCoordinates, HermiteH, JacobiP, NumericalOrder, NumericalSort, PrincipalComponents, RealValuedNumericQ, ReIm, SawtoothWave, StringForm, ToSphericalCoordinates` with status `PARTIAL` support
- new functions: `ClebschGordan, ThreeJSymbol` with status `EXPERIMENTAL` support
- use hipparchus `PowellOptimizer` for non-linear functions in `NMinimize,NMaximize`
- new function `PearsonCorrelationTest` for two vectors 
- icu4j library moved from Maven module `matheclipse-io` to new module `matheclipse-nlp` 
- implemented `CompleteBipartiteGraphGenerator` in function `CompleteGraph` 
- improved `N` function for `Rule, RuleDelayed` and `Association` arguments (#824)
- define parser input `\[ExponentialE]` as `E`
- improved `TeXForm` for `EulerE`
- decoupled rules creation from symbol creation in `RulePreprocessor` implementation
- improved `LinearModelFit` for vector inputs 
- improved performance for `Nest, NestWhileList`
- built-in functions can have an `ImplementationStatus` 
- fix bug in `Refine` for `Sin, Csc`
- improved `LaplaceTransform` and `InverseLaplaceTransform` with numerical calculations 
- define new `TeXParser` class based on SnuggleTeX implementation
- use choco solver for solving `Integers` domain equations in `Solve`
- added `GenerateConditions->True` option to Solve (especially for trigonometric functions)
- new class MD2Symja - render Markdown to HTML
- implemented PrecedenceForm, Infix, Prefix, Postfix  
- new opened/closed ends interval data object `IntervalData({min-value, Less/LessEqual, Less/LessEqual, max-value})`
- implemented `NormalMatrixQ, FourierDCTMAtrix, FourierDSTMatrix`
- implemented `AASTriangle, ASATriangle, SASTriangle,SSSTriangle`
- new `ArcLength, Area, Perimeter, Volume` functions
- use [github.com/jsxgraph/json2D_JSXGrap](https://github.com/jsxgraph/json2D_JSXGraph) for 2D `Graphics, DiscretePlot, ListLinePlot, ListPlot, LogPlot, LogLogPlot, LogLinearPlot, ListLogPlot, ListLogLinearPlot, ListLogLogPlot` objects
- new `TrigSimplifyFu` function (#498)
- new `TransformationFunction, RotationTransform, ScalingTranform, ShearingTransform, TranslationTransform` (#583)
- new `SequenceCases, SequenceReplace, SequenceSplit`
- SymjaBot - Discord bot
- new `KroneckerProduct, Hyperfactorial, LowerTriangularMatrixQ, UnitaryMatrixQ, UpperTriangularMatrixQ`
- new `CoordinateBounds, ArrayFlatten, HessenbergDecomposition, SchurDecomposition`
- implemented multivariate Newton’s method in the `FindRoot` function (#566)
- new `ModularInverse, CompositeQ, ConvexHullMesh, CoordinateBoundingBox, QuantityUnit, PadeApproximant, FactorTermsList`
- new `BooleanFunction` (#527) 
- jbang enablement (#515)
- new `FactorialPower, LerchPhi, HurwitzLerchPhi, Hypergeometric1F1, HypergeometricPFQ`
- implemented Kryo serializer and deserializer (#514)
- new `InverseJacobi...` functions (#501)

## [v2.0.0](https://github.com/axkr/symja_android_library/releases/tag/v2.0.0) - 2022-03-12

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

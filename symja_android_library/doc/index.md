
- [Installation](#installation)
- [Basic calculations](#basic-calculations)
- [Expression types](#expression-types)
- [Functions Reference](#functions-reference) 

## Installation

Download the latest release from 
* [github.com/axkr/symja_android_library/releases](https://github.com/axkr/symja_android_library/releases)

unzip the download in a separate folder and modify the symja.bat file to use your Java 8 installation path to run the Symja console:
```
"C:\Program Files\Java\jdk1.8.0_25\bin\java" -classpath "lib/*" org.matheclipse.core.eval.Console 
```

## Basic calculations

Symja can be used to calculate basic stuff:

```
1 + 2
```

To submit a command to Symja, press Shift+Return in the Web interface or Return in the console interface. The result will be printed in a new line below your query.

Symja understands all basic arithmetic operators and applies the usual operator precedence. Use parentheses when needed:
```
1 - 2 * (3 + 5) / 4
```

The multiplication can be omitted:
```
1 - 2 (3 + 5) / 4
```

But function `f(x)` notation isn't interpreted as `f*(x)`
```
f(x)
```

Powers can be entered using ^:
```
3 ^ 4
```

Integer divisions yield rational numbers:
```
6 / 4
```

To convert the result to a floating point number, apply the function `N`:
```
N(6 / 4)
```

As you can see, functions are applied using the identifier `N` and parentheses `(` and `)`. 
In general an identifier is a user-defined or built-in name for a variable, function or constant. 
Only the identifiers which consists of only one character are case sensitive. 
For all other identifiers the input parser doesn't distinguish between lower and upper case characters.

For example: the upper-case identifiers [D](functions/D.md), [E](functions/E.md), [I](functions/I.md), [N](functions/N.md), 
are different from the identifiers `d, e, i, n`, whereas the 
functions like [Factorial](functions/Factorial.md), [Integrate](functions/Integrate.md) can be entered as 
`factorial(100)` or `integrate(sin(x),x)`. If you type `SIN(x)` or `sin(x)`, 
Symja assumes you always mean the same built-in [Sin](functions/Sin.md) function.  

Symja provides many common mathematical functions and constants, e.g.:
```
Log(E)

Sin(Pi)

Cos(0.5)
```
When entering floating point numbers in your query, Symja will perform a numerical evaluation and present a numerical result, pretty much like if you had applied N.

Of course, Symja has complex numbers:
```
Sqrt(-4)

I ^ 2

(3 + 2*I) ^ 4

(3 + 2*I) ^ (2.5 - I)

Tan(I + 0.5)
```

Abs calculates absolute values:
```
Abs(-3)

Abs(3 + 4*I)
```

Symja can operate with pretty huge numbers:
```
100!
```

(! denotes the factorial function.) The precision of numerical evaluation can be set:

```
N(Pi, 100)
```

Division by zero is forbidden:
```
1 / 0
```

Other expressions involving Infinity are evaluated:
```
Infinity + 2 Infinity
```

In contrast to combinatorial belief, 0^0 is undefined:
```
0 ^ 0
```

The result of the previous query to Symja can be accessed by %:
```
3 + 4
```

In the console available functions can be determined with the `?` operator

```
?ArcC*
ArcCos, ArcCosh, ArcCot, ArcCoth, ArcCsc, ArcCsch
```

Documentation can be displayed by asking for information for the function name.

```
?Integrate
```

## Expression types

| Type          		| Description         																							| Input Example 						|
|:---------------------:|:-------------------------------------------------------------------------------------------------------------:|:-------------------------------------:|
| Integer				| integer numbers   																							| `42`  								|
| Rational				| rational numbers        																						| `13/17` 								|
| Complex				| complex numbers      			 																				| `2+I*1/3`    							|
| Real					| double values  																								| `0.5`  								|
| Complex Real			| complex double values  																						| `0.5-I*0.25`  						|
| Evaluation Precedence	| control precedence with `(...)`  																				| `(a+b)*c`  							|
| Lists					| comma separated list of elements which are sourrounded by `{ ... }`  											| `{a, b, c, d} `  						|
| Vectors				| vectors are like list, but cannot contain sublists `{ ... }`  												| `{1, 2, 3, 4}`  						|
| Matrices				| a matrix contains the rows as sublists 																		| `{{1, 2}, {3, 4}}`  					|
| Predefined Functions	| predefined function names start with an upper case character and the arguments are surrounded by `( ... )`	| `Sin(0), PrimeQ(13)` 					|
| Predefined Constants	| predefined constant names start with an upper case character 													| `Degree, E, Pi, False, True, ... `	|
| Userdefined variables	| identifiers which you would like to assign a value start with a `$` character 								| `$a=42`  								|
| Userdefined rules		| identifiers which you would like to assign a rule start with a `$` character  								| `$f(x_,y_):={x,y}`  					|
| Pattern Symbols		| patterns end with a preceding `_` and could have a constraint 												| `$f(x_IntegerQ):={x}`  				|
| Strings				| character strings are enclosed by double quote characters  													| `"Hello World"`  						|
| Slots					| a `#` character followed by an optional number 																| `#` or `#2`   						|
| Pure Functions		| pure functions can be expressed with the `&` operator															| `(#^3)&[x]`  gives `x^3` 				|
| Parts of an expression| `expr[[index]]`   																							| `{a, b, c, d}[[2]]`  gives `b`		|

## Reference of built-in symbols

* [Control statements](control-statements.md)
* [Linear algebra](linear-algebra.md)
* [Number theoretic functions](number-theoretic-functions.md)
* [Patterns and rules](patterns-and-rules.md)

## Functions Reference

* [Abort](functions/Abort.md)
* [Abs](functions/Abs.md)
* [AddTo](functions/AddTo.md)
* [All](functions/All.md)
* [AllTrue](functions/AllTrue.md)
* [Alternatives](functions/Alternatives.md)
* [And](functions/And.md)
* [AngleVector](functions/AngleVector.md)
* [AnyTrue](functions/AnyTrue.md)
* [Apart](functions/Apart.md)
* [Append](functions/Append.md)
* [AppendTo](functions/AppendTo.md)
* [Apply](functions/Apply.md)
* [ArcCos](functions/ArcCos.md)
* [ArcCosh](functions/ArcCosh.md)
* [ArcCot](functions/ArcCot.md)
* [ArcCoth](functions/ArcCoth.md)
* [ArcCsc](functions/ArcCsc.md)
* [ArcCsch](functions/ArcCsch.md)
* [ArcSec](functions/ArcSec.md)
* [ArcSech](functions/ArcSech.md)
* [ArcSin](functions/ArcSin.md)
* [ArcSinh](functions/ArcSinh.md)
* [ArcTan](functions/ArcTan.md)
* [ArcTanh](functions/ArcTanh.md)
* [Arg](functions/Arg.md)
* [Array](functions/Array.md)
* [ArrayDepth](functions/ArrayDepth.md)
* [ArrayQ](functions/ArrayQ.md)
* [AtomQ](functions/AtomQ.md)
* [Attributes](functions/Attributes.md)
* [BernoulliB](functions/BernoulliB.md)
* [Binomial](functions/Binomial.md)
* [BitLength](functions/BitLength.md)
* [Block](functions/Block.md)
* [Boole](functions/Boole.md)
* [BooleanMinimize](functions/BooleanMinimize.md)
* [BooleanQ](functions/BooleanQ.md)
* [Booleans](functions/Booleans.md)
* [BrayCurtisDistance](functions/BrayCurtisDistance.md)
* [Break](functions/Break.md)
* [CanberraDistance](functions/CanberraDistance.md)
* [Cancel](functions/Cancel.md)
* [CartesianProduct](functions/CartesianProduct.md)
* [Cases](functions/Cases.md)
* [CatalanNumber](functions/CatalanNumber.md)
* [Catenate](functions/Catenate.md)
* [Ceiling](functions/Ceiling.md)
* [CharacteristicPolynomial](functions/CharacteristicPolynomial.md)
* [ChebyshevT](functions/ChebyshevT.md)
* [ChebyshevU](functions/ChebyshevU.md)
* [ChessboardDistance](functions/ChessboardDistance.md)
* [Chop](functions/Chop.md)
* [Clear](functions/Clear.md)
* [ClearAll](functions/ClearAll.md)
* [ClearAttributes](functions/ClearAttributes.md)
* [Coefficient](functions/Coefficient.md)
* [CoefficientList](functions/CoefficientList.md)
* [Collect](functions/Collect.md)
* [Complex](functions/Complex.md)
* [Complexes](functions/Complexes.md)
* [ComplexExpand](functions/ComplexExpand.md)
* [ComplexInfinity](functions/ComplexInfinity.md)
* [ComposeList](functions/ComposeList.md)
* [CompoundExpression](functions/CompoundExpression.md)
* [Condition](functions/Condition.md)
* [Conjugate](functions/Conjugate.md)
* [ConjugateTranspose](functions/ConjugateTranspose.md)
* [Constant](functions/Constant.md)
* [ConstantArray](functions/ConstantArray.md)
* [Continue](functions/Continue.md)
* [ContinuedFraction](functions/ContinuedFraction.md)
* [CoprimeQ](functions/CoprimeQ.md)
* [Cos](functions/Cos.md)
* [Cosh](functions/Cosh.md)
* [CosineDistance](functions/CosineDistance.md)
* [Cot](functions/Cot.md)
* [Coth](functions/Coth.md)
* [Count](functions/Count.md)
* [Cross](functions/Cross.md)
* [Csc](functions/Csc.md)
* [Csch](functions/Csch.md)
* [CubeRoot](functions/CubeRoot.md)
* [Curl](functions/Curl.md)
* [D](functions/D.md)
* [Decrement](functions/Decrement.md)
* [Defer](functions/Defer.md)
* [Definition](functions/Definition.md)
* [Degree](functions/Degree.md)
* [DeleteCases](functions/DeleteCases.md)
* [DeleteDuplicates](functions/DeleteDuplicates.md)
* [Denominator](functions/Denominator.md)
* [Depth](functions/Depth.md)
* [Derivative](functions/Derivative.md)
* [DesignMatrix](functions/DesignMatrix.md)
* [Det](functions/Det.md)
* [DiagonalMatrix](functions/DiagonalMatrix.md)
* [DiceDissimilarity](functions/DiceDissimilarity.md)
* [Diff](functions/Diff.md)
* [DigitQ](functions/DigitQ.md)
* [Dimensions](functions/Dimensions.md)
* [DirectedInfinity](functions/DirectedInfinity.md)
* [Discriminant](functions/Discriminant.md)
* [Distribute](functions/Distribute.md)
* [Divergence](functions/Divergence.md)
* [Divide](functions/Divide.md)
* [DivideBy](functions/DivideBy.md)
* [Divisors](functions/Divisors.md)
* [Do](functions/Do.md)
* [Dot](functions/Dot.md)
* [Drop](functions/Drop.md)
* [DSolve](functions/DSolve.md)
* [E](functions/E.md)
* [Eigenvalues](functions/Eigenvalues.md)
* [Eigenvectors](functions/Eigenvectors.md)
* [Eliminate](functions/Eliminate.md)
* [Equal](functions/Equal.md)
* [Equivalent](functions/Equivalent.md)
* [Erf](functions/Erf.md)
* [Erfc](functions/Erfc.md)
* [EuclideanDistance](functions/EuclideanDistance.md)
* [EulerE](functions/EulerE.md)
* [EulerPhi](functions/EulerPhi.md)
* [EvalF](functions/EvalF.md)
* [EvenQ](functions/EvenQ.md)
* [ExactNumberQ](functions/ExactNumberQ.md)
* [Except](functions/Except.md)
* [Exp](functions/Exp.md)
* [Expand](functions/Expand.md)
* [ExpandAll](functions/ExpandAll.md)
* [Exponent](functions/Exponent.md)
* [Extract](functions/Extract.md)
* [Factor](functions/Factor.md)
* [Factorial](functions/Factorial.md)
* [Factorial2](functions/Factorial2.md)
* [FactorInteger](functions/FactorInteger.md)
* [FactorSquareFree](functions/FactorSquareFree.md)
* [FactorSquareFreeList](functions/FactorSquareFreeList.md)
* [FactorTerms](functions/FactorTerms.md)
* [False](functions/False.md)
* [Fibonacci](functions/Fibonacci.md)
* [FindRoot](functions/FindRoot.md)
* [First](functions/First.md)
* [Fit](functions/Fit.md)
* [FixedPoint](functions/FixedPoint.md)
* [FixedPointList](functions/FixedPointList.md)
* [Flat](functions/Flat.md)
* [Flatten](functions/Flatten.md)
* [Floor](functions/Floor.md)
* [For](functions/For.md)
* [FractionalPart](functions/FractionalPart.md)
* [FreeQ](functions/FreeQ.md)
* [FrobeniusSolve](functions/FrobeniusSolve.md)
* [Gamma](functions/Gamma.md)
* [GCD](functions/GCD.md)
* [GoldenRatio](functions/GoldenRatio.md)
* [Greater](functions/Greater.md)
* [GreaterEqual](functions/GreaterEqual.md)
* [HarmonicNumber](functions/HarmonicNumber.md)
* [Haversine](functions/Haversine.md)
* [Head](functions/Head.md)
* [HermiteH](functions/HermiteH.md)
* [HilbertMatrix](functions/HilbertMatrix.md)
* [Hold](functions/Hold.md)
* [HoldAll](functions/HoldAll.md)
* [HoldFirst](functions/HoldFirst.md)
* [HoldForm](functions/HoldForm.md)
* [HoldRest](functions/HoldRest.md)
* [I](functions/I.md)
* [IdentityMatrix](functions/IdentityMatrix.md)
* [If](functions/If.md)
* [Im](functions/Im.md)
* [Implies](functions/Implies.md)
* [Increment](functions/Increment.md)
* [Indeterminate](functions/Indeterminate.md)
* [InexactNumberQ](functions/InexactNumberQ.md)
* [Infinity](functions/Infinity.md)
* [Inner](functions/Inner.md)
* [Int](functions/Int.md)
* [Integer](functions/Integer.md)
* [IntegerExponent](functions/IntegerExponent.md)
* [IntegerLength](functions/IntegerLength.md)
* [IntegerQ](functions/IntegerQ.md)
* [Integers](functions/Integers.md)
* [Integrate](functions/Integrate.md)
* [InterpolatingPolynomial](functions/InterpolatingPolynomial.md)
* [Intersection](functions/Intersection.md)
* [Inverse](functions/Inverse.md)
* [InverseErf](functions/InverseErf.md)
* [InverseErfc](functions/InverseErfc.md)
* [InverseHaversine](functions/InverseHaversine.md)
* [JaccardDissimilarity](functions/JaccardDissimilarity.md)
* [JacobiMatrix](functions/JacobiMatrix.md)
* [JacobiSymbol](functions/JacobiSymbol.md)
* [JavaForm](functions/JavaForm.md)
* [Join](functions/Join.md)
* [Khinchin](functions/Khinchin.md)
* [LaguerreL](functions/LaguerreL.md)
* [Last](functions/Last.md)
* [LCM](functions/LCM.md)
* [LegendreP](functions/LegendreP.md)
* [Length](functions/Length.md)
* [Less](functions/Less.md)
* [LessEqual](functions/LessEqual.md)
* [LetterQ](functions/LetterQ.md)
* [Level](functions/Level.md)
* [LevelQ](functions/LevelQ.md)
* [Limit](functions/Limit.md)
* [LinearProgramming](functions/LinearProgramming.md)
* [LinearSolve](functions/LinearSolve.md)
* [List](functions/List.md)
* [Listable](functions/Listable.md)
* [ListQ](functions/ListQ.md)
* [Log](functions/Log.md)
* [Log10](functions/Log10.md)
* [Log2](functions/Log2.md)
* [LogisticSigmoid](functions/LogisticSigmoid.md)
* [LUDecomposition](functions/LUDecomposition.md)
* [MachineNumberQ](functions/MachineNumberQ.md)
* [ManhattanDistance](functions/ManhattanDistance.md)
* [Map](functions/Map.md)
* [MapThread](functions/MapThread.md)
* [MatchingDissimilarity](functions/MatchingDissimilarity.md)
* [MatchQ](functions/MatchQ.md)
* [MatrixPower](functions/MatrixPower.md)
* [MatrixQ](functions/MatrixQ.md)
* [MatrixRank](functions/MatrixRank.md)
* [Max](functions/Max.md)
* [MemberQ](functions/MemberQ.md)
* [Min](functions/Min.md)
* [Minus](functions/Minus.md)
* [Mod](functions/Mod.md)
* [MoebiusMu](functions/MoebiusMu.md)
* [MonomialList](functions/MonomialList.md)
* [Most](functions/Most.md)
* [Multinomial](functions/Multinomial.md)
* [N](functions/N.md)
* [Nest](functions/Nest.md)
* [NestList](functions/NestList.md)
* [NestWhile](functions/NestWhile.md)
* [NextPrime](functions/NextPrime.md)
* [NHoldAll](functions/NHoldAll.md)
* [NHoldFirst](functions/NHoldFirst.md)
* [NHoldRest](functions/NHoldRest.md)
* [NIntegrate](functions/NIntegrate.md)
* [None](functions/None.md)
* [NoneTrue](functions/NoneTrue.md)
* [NonNegative](functions/NonNegative.md)
* [Norm](functions/Norm.md)
* [Normalize](functions/Normalize.md)
* [Not](functions/Not.md)
* [NRoots](functions/NRoots.md)
* [Null](functions/Null.md)
* [NullSpace](functions/NullSpace.md)
* [NumberQ](functions/NumberQ.md)
* [Numerator](functions/Numerator.md)
* [OddQ](functions/OddQ.md)
* [OneIdentity](functions/OneIdentity.md)
* [Operate](functions/Operate.md)
* [Optional](functions/Optional.md)
* [Or](functions/Or.md)
* [Order](functions/Order.md)
* [OrderedQ](functions/OrderedQ.md)
* [Orderless](functions/Orderless.md)
* [Outer](functions/Outer.md)
* [PadLeft](functions/PadLeft.md)
* [PadRight](functions/PadRight.md)
* [Part](functions/Part.md)
* [Partition](functions/Partition.md)
* [PatternTest](functions/PatternTest.md)
* [Pi](functions/Pi.md)
* [Piecewise](functions/Piecewise.md)
* [Plus](functions/Plus.md)
* [Pochhammer](functions/Pochhammer.md)
* [PolynomialExtendedGCD](functions/PolynomialExtendedGCD.md)
* [PolynomialGCD](functions/PolynomialGCD.md)
* [PolynomialLCM](functions/PolynomialLCM.md)
* [PolynomialQ](functions/PolynomialQ.md)
* [PolynomialQuotient](functions/PolynomialQuotient.md)
* [PolynomialQuotientRemainder](functions/PolynomialQuotientRemainder.md)
* [PolynomialRemainder](functions/PolynomialRemainder.md)
* [Position](functions/Position.md)
* [Positive](functions/Positive.md)
* [Power](functions/Power.md)
* [PowerExpand](functions/PowerExpand.md)
* [PowerMod](functions/PowerMod.md)
* [PreDecrement](functions/PreDecrement.md)
* [PreIncrement](functions/PreIncrement.md)
* [Prepend](functions/Prepend.md)
* [PrependTo](functions/PrependTo.md)
* [Prime](functions/Prime.md)
* [PrimeOmega](functions/PrimeOmega.md)
* [PrimePi](functions/PrimePi.md)
* [PrimePowerQ](functions/PrimePowerQ.md)
* [PrimeQ](functions/PrimeQ.md)
* [Product](functions/Product.md)
* [ProductLog](functions/ProductLog.md)
* [PseudoInverse](functions/PseudoInverse.md)
* [QRDecomposition](functions/QRDecomposition.md)
* [Quotient](functions/Quotient.md)
* [Range](functions/Range.md)
* [Rational](functions/Rational.md)
* [Re](functions/Re.md)
* [Real](functions/Real.md)
* [RealNumberQ](functions/RealNumberQ.md)
* [Reals](functions/Reals.md)
* [Refine](functions/Refine.md)
* [ReplacePart](functions/ReplacePart.md)
* [Rest](functions/Rest.md)
* [Return](functions/Return.md)
* [Riffle](functions/Riffle.md)
* [RogersTanimotoDissimilarity](functions/RogersTanimotoDissimilarity.md)
* [RowReduce](functions/RowReduce.md)
* [Rule](functions/Rule.md)
* [RuleDelayed](functions/RuleDelayed.md)
* [RussellRaoDissimilarity](functions/RussellRaoDissimilarity.md)
* [SameQ](functions/SameQ.md)
* [Scan](functions/Scan.md)
* [Sec](functions/Sec.md)
* [Sech](functions/Sech.md)
* [Select](functions/Select.md)
* [Set](functions/Set.md)
* [SetAttributes](functions/SetAttributes.md)
* [SetDelayed](functions/SetDelayed.md)
* [Sign](functions/Sign.md)
* [Simplify](functions/Simplify.md)
* [Sin](functions/Sin.md)
* [SingularValueDecomposition](functions/SingularValueDecomposition.md)
* [Sinh](functions/Sinh.md)
* [SokalSneathDissimilarity](functions/SokalSneathDissimilarity.md)
* [Solve](functions/Solve.md)
* [Sort](functions/Sort.md)
* [Span](functions/Span.md)
* [Split](functions/Split.md)
* [SplitBy](functions/SplitBy.md)
* [Sqrt](functions/Sqrt.md)
* [SquaredEuclideanDistance](functions/SquaredEuclideanDistance.md)
* [SquareFreeQ](functions/SquareFreeQ.md)
* [StirlingS1](functions/StirlingS1.md)
* [StirlingS2](functions/StirlingS2.md)
* [StruveH](functions/StruveH.md)
* [StruveL](functions/StruveL.md)
* [Subsets](functions/Subsets.md)
* [Subtract](functions/Subtract.md)
* [SubtractFrom](functions/SubtractFrom.md)
* [Sum](functions/Sum.md)
* [Switch](functions/Switch.md)
* [Symbol](functions/Symbol.md)
* [SymbolName](functions/SymbolName.md)
* [SymbolQ](functions/SymbolQ.md)
* [Table](functions/Table.md)
* [Take](functions/Take.md)
* [Tan](functions/Tan.md)
* [Tanh](functions/Tanh.md)
* [TeXForm](functions/TeXForm.md)
* [Thread](functions/Thread.md)
* [Through](functions/Through.md)
* [Times](functions/Times.md)
* [TimesBy](functions/TimesBy.md)
* [Together](functions/Together.md)
* [Tr](functions/Tr.md)
* [Trace](functions/Trace.md)
* [Transpose](functions/Transpose.md)
* [TrigExpand](functions/TrigExpand.md)
* [TrigReduce](functions/TrigReduce.md)
* [TrigToExp](functions/TrigToExp.md)
* [True](functions/True.md)
* [TrueQ](functions/TrueQ.md)
* [Tuples](functions/Tuples.md)
* [Unequal](functions/Unequal.md)
* [Union](functions/Union.md)
* [UnitStep](functions/UnitStep.md)
* [UnitVector](functions/UnitVector.md)
* [UnsameQ](functions/UnsameQ.md)
* [ValueQ](functions/ValueQ.md)
* [VandermondeMatrix](functions/VandermondeMatrix.md)
* [Variables](functions/Variables.md)
* [VectorAngle](functions/VectorAngle.md)
* [VectorQ](functions/VectorQ.md)
* [Which](functions/Which.md)
* [While](functions/While.md)
* [Xor](functions/Xor.md)
* [YuleDissimilarity](functions/YuleDissimilarity.md)
* [Zeta](functions/Zeta.md)

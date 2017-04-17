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



## Functions Reference

* [Abs](functions/Abs.md)
* [All](functions/All.md)
* [AllTrue](functions/AllTrue.md)
* [And](functions/And.md)
* [AnyTrue](functions/AnyTrue.md)
* [Apart](functions/Apart.md)
* [Apply](functions/Apply.md)
* [Array](functions/Array.md)
* [ArrayDepth](functions/ArrayDepth.md)
* [ArrayQ](functions/ArrayQ.md)
* [AtomQ](functions/AtomQ.md)
* [Attributes](functions/Attributes.md)
* [BernoulliB](functions/BernoulliB.md)
* [Binomial](functions/Binomial.md)
* [Block](functions/Block.md)
* [Boole](functions/Boole.md)
* [BooleanMinimize](functions/BooleanMinimize.md)
* [BooleanQ](functions/BooleanQ.md)
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
* [ChessboardDistance](functions/ChessboardDistance.md)
* [Chop](functions/Chop.md)
* [Coefficient](functions/Coefficient.md)
* [CoefficientList](functions/CoefficientList.md)
* [Collect](functions/Collect.md)
* [CompoundExpression](functions/CompoundExpression.md)
* [ConjugateTranspose](functions/ConjugateTranspose.md)
* [ConstantArray](functions/ConstantArray.md)
* [Continue](functions/Continue.md)
* [ContinuedFraction](functions/ContinuedFraction.md)
* [CoprimeQ](functions/CoprimeQ.md)
* [Cos](functions/Cos.md)
* [CosineDistance](functions/CosineDistance.md)
* [Cot](functions/Cot.md)
* [Count](functions/Count.md)
* [Cross](functions/Cross.md)
* [CubeRoot](functions/CubeRoot.md)
* [Curl](functions/Curl.md)
* [D](functions/D.md)
* [Defer](functions/Defer.md)
* [Degree](functions/Degree.md)
* [DeleteCases](functions/DeleteCases.md)
* [DeleteDuplicates](functions/DeleteDuplicates.md)
* [Denominator](functions/Denominator.md)
* [Depth](functions/Depth.md)
* [DesignMatrix](functions/DesignMatrix.md)
* [Det](functions/Det.md)
* [DiagonalMatrix](functions/DiagonalMatrix.md)
* [DiceDissimilarity](functions/DiceDissimilarity.md)
* [DigitQ](functions/DigitQ.md)
* [Dimensions](functions/Dimensions.md)
* [Discriminant](functions/Discriminant.md)
* [Distribute](functions/Distribute.md)
* [Divergence](functions/Divergence.md)
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
* [EuclideanDistance](functions/EuclideanDistance.md)
* [EulerE](functions/EulerE.md)
* [EulerPhi](functions/EulerPhi.md)
* [EvenQ](functions/EvenQ.md)
* [Expand](functions/Expand.md)
* [ExpandAll](functions/ExpandAll.md)
* [Extract](functions/Extract.md)
* [Factor](functions/Factor.md)
* [Factorial](functions/Factorial.md)
* [FactorInteger](functions/FactorInteger.md)
* [Fibonacci](functions/Fibonacci.md)
* [FindRoot](functions/FindRoot.md)
* [First](functions/First.md)
* [FixedPoint](functions/FixedPoint.md)
* [FixedPointList](functions/FixedPointList.md)
* [Flatten](functions/Flatten.md)
* [Floor](functions/Floor.md)
* [For](functions/For.md)
* [FreeQ](functions/FreeQ.md)
* [GCD](functions/GCD.md)
* [Greater](functions/Greater.md)
* [GreaterEqual](functions/GreaterEqual.md)
* [Head](functions/Head.md)
* [Hold](functions/Hold.md)
* [HoldForm](functions/HoldForm.md)
* [I](functions/I.md)
* [IdentityMatrix](functions/IdentityMatrix.md)
* [If](functions/If.md)
* [Implies](functions/Implies.md)
* [Inner](functions/Inner.md)
* [IntegerExponent](functions/IntegerExponent.md)
* [Integrate](functions/Integrate.md)
* [InterpolatingPolynomial](functions/InterpolatingPolynomial.md)
* [Inverse](functions/Inverse.md)
* [JaccardDissimilarity](functions/JaccardDissimilarity.md)
* [JavaForm](functions/JavaForm.md)
* [Join](functions/Join.md)
* [Last](functions/Last.md)
* [LCM](functions/LCM.md)
* [Length](functions/Length.md)
* [Less](functions/Less.md)
* [LessEqual](functions/LessEqual.md)
* [Level](functions/Level.md)
* [LevelQ](functions/LevelQ.md)
* [LinearSolve](functions/LinearSolve.md)
* [List](functions/List.md)
* [ListQ](functions/ListQ.md)
* [Log](functions/Log.md)
* [LUDecomposition](functions/LUDecomposition.md)
* [ManhattanDistance](functions/ManhattanDistance.md)
* [Map](functions/Map.md)
* [MapThread](functions/MapThread.md)
* [MatchingDissimilarity](functions/MatchingDissimilarity.md)
* [MatrixPower](functions/MatrixPower.md)
* [MatrixQ](functions/MatrixQ.md)
* [MatrixRank](functions/MatrixRank.md)
* [Max](functions/Max.md)
* [MemberQ](functions/MemberQ.md)
* [Min](functions/Min.md)
* [Mod](functions/Mod.md)
* [Most](functions/Most.md)
* [Multinomial](functions/Multinomial.md)
* [N](functions/N.md)
* [Nest - Kopie](functions/Nest - Kopie.md)
* [Nest](functions/Nest.md)
* [NestList](functions/NestList.md)
* [NestWhile](functions/NestWhile.md)
* [NextPrime](functions/NextPrime.md)
* [None](functions/None.md)
* [NoneTrue](functions/NoneTrue.md)
* [NonNegative](functions/NonNegative.md)
* [Norm](functions/Norm.md)
* [Normalize](functions/Normalize.md)
* [Not](functions/Not.md)
* [NRoots](functions/NRoots.md)
* [Null](functions/Null.md)
* [NullSpace](functions/NullSpace.md)
* [Numerator](functions/Numerator.md)
* [OddQ](functions/OddQ.md)
* [Operate](functions/Operate.md)
* [Or](functions/Or.md)
* [OrderedQ](functions/OrderedQ.md)
* [Outer](functions/Outer.md)
* [Part](functions/Part.md)
* [Partition](functions/Partition.md)
* [Position](functions/Position.md)
* [Positive](functions/Positive.md)
* [PowerExpand](functions/PowerExpand.md)
* [PowerMod](functions/PowerMod.md)
* [Prime](functions/Prime.md)
* [PrimePowerQ](functions/PrimePowerQ.md)
* [PrimeQ](functions/PrimeQ.md)
* [PseudoInverse](functions/PseudoInverse.md)
* [QRDecomposition](functions/QRDecomposition.md)
* [Quotient](functions/Quotient.md)
* [Range](functions/Range.md)
* [ReplacePart](functions/ReplacePart.md)
* [Rest](functions/Rest.md)
* [Return](functions/Return.md)
* [RogersTanimotoDissimilarity](functions/RogersTanimotoDissimilarity.md)
* [RowReduce](functions/RowReduce.md)
* [Rule](functions/Rule.md)
* [RuleDelayed](functions/RuleDelayed.md)
* [RussellRaoDissimilarity](functions/RussellRaoDissimilarity.md)
* [SameQ](functions/SameQ.md)
* [Scan](functions/Scan.md)
* [Select](functions/Select.md)
* [Simplify](functions/Simplify.md)
* [Sin](functions/Sin.md)
* [SingularValueDecomposition](functions/SingularValueDecomposition.md)
* [SokalSneathDissimilarity](functions/SokalSneathDissimilarity.md)
* [Solve](functions/Solve.md)
* [Sort](functions/Sort.md)
* [Span](functions/Span.md)
* [Split](functions/Split.md)
* [SplitBy](functions/SplitBy.md)
* [SquaredEuclideanDistance](functions/SquaredEuclideanDistance.md)
* [SquareFreeQ](functions/SquareFreeQ.md)
* [Subsets](functions/Subsets.md)
* [Switch](functions/Switch.md)
* [Symbol](functions/Symbol.md)
* [SymbolName](functions/SymbolName.md)
* [SymbolQ](functions/SymbolQ.md)
* [Table](functions/Table.md)
* [Take](functions/Take.md)
* [Tan](functions/Tan.md)
* [Thread](functions/Thread.md)
* [Through](functions/Through.md)
* [Together](functions/Together.md)
* [Trace](functions/Trace.md)
* [Transpose](functions/Transpose.md)
* [TrueQ](functions/TrueQ.md)
* [Unequal](functions/Unequal.md)
* [UnsameQ](functions/UnsameQ.md)
* [ValueQ](functions/ValueQ.md)
* [Variables](functions/Variables.md)
* [VectorAngle](functions/VectorAngle.md)
* [VectorQ](functions/VectorQ.md)
* [Which](functions/Which.md)
* [While](functions/While.md)
* [Xor](functions/Xor.md)
* [YuleDissimilarity](functions/YuleDissimilarity.md)


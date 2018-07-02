# How to evaluate functions

## Overview
The Symja constants are implemented in the package:

* [org.matheclipse.core.builtin.ConstantDefinitions](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ConstantDefinitions.java)

The Symja functions are implemented in the packages:

* [org.matheclipse.core.builtin](https://github.com/axkr/symja_android_library/tree/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin)
* [org.matheclipse.core.reflection.system](https://github.com/axkr/symja_android_library/tree/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system)

Built-in functions must implement the interface `ICoreFunctionEvaluator` and evaluate the arguments in a non standard way.

System functions must implement the interface `IFunctionEvaluator` and are evaluated according to the assigned function symbol attributes defined in the `IFunctionEvaluator#setUp()` method.
A lot of system functions have associated pattern matching rule files in the folder:

* [symja_android_library/rules](https://github.com/axkr/symja_android_library/tree/master/symja_android_library/rules)

These rules are converted by the class [org.matheclipse.core.preprocessor.RulePreprocessor](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/tools/src/main/java/org/matheclipse/core/preprocessor/RulePreprocessor.java)
into the package [org.matheclipse.core.reflection.system.rules](https://github.com/axkr/symja_android_library/tree/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/rules)

A new function name should be added to `org.matheclipse.core.convert.AST2Expr#FUNCTION_STRINGS` `String[]` array, with a leading upper case character in its name.

## Example evaluating `D(<expression>, x)`

In the following section the evaluation and implementation of the partial derivative function is described.

Example usage:

```java
>> D(sin(x)^3 + x, x)
1+3*Sin(x)^2*Cos(x)
```

To see the intermediate evaluation steps you can use the `Trace( )` function

```java
>> Trace(D(sin(x)^3 + x, x)) 
```

Internally the `D(<expression>, x)` function uses the `Derivative( )` function. Both function names are defined in 

* [org.matheclipse.core.convert.AST2Expr#FUNCTION_STRINGS](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/convert/AST2Expr.java)


Both of these built-in functions are defined in the `org.matheclipse.core.reflection.system` package:

* [org.matheclipse.core.reflection.system.Derivative](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/Derivative.java)
* [org.matheclipse.core.reflection.system.D](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/D.java)


The [Derivative( ) rules](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/rules/DerivativeRules.m) are converted by the class [org.matheclipse.core.preprocessor.RulePreprocessor](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/tools/src/main/java/org/matheclipse/core/preprocessor/RulePreprocessor.java)
into the rules java file [org.matheclipse.core.reflection.system.rules.DerivativeRules](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/rules/DerivativeRules.java).

To use these functions (or other built-in constants and functions) from within other java sources you can import 
[org.matheclipse.core.expression.F](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/expression/F.java)

For `D` and `Derivative` there are two static methods defined:

```java
public static IAST D(final IExpr a0, final IExpr a1)

public static IAST Derivative(final IExpr... a)

```

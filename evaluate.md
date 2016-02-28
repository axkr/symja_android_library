## How to evaluate functions

The Symja constants are implemented in the package:

* [org.matheclipse.core.builtin.constant](https://bitbucket.org/axelclk/symja_android_library/src/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/constant/)

The Symja functions are implemented in the packages:

* [org.matheclipse.core.builtin.function](https://bitbucket.org/axelclk/symja_android_library/src/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/function/)
* [org.matheclipse.core.reflection.system](https://bitbucket.org/axelclk/symja_android_library/src/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/)

Builtin functions must implement the interface `ICoreFunctionEvaluator` and evaluate the arguments in a non standard way.

System functions must implement the interface `IFunctionEvaluator` and are evaluated according to the assigned function symbol attributes defined in the `IFunctionEvaluator#setUp()` method.
A lot of system functions have associated pattern matching rule files in the folder:

* [symja_android_library/rules](https://bitbucket.org/axelclk/symja_android_library/src/master/symja_android_library/rules/)

These rules are converted by the class [org.matheclipse.core.preprocessor.RulePreprocessor](https://bitbucket.org/axelclk/symjaunittests/src/master/SymjaUnitTests/src/org/matheclipse/core/preprocessor/RulePreprocessor.java)
into the package [org.matheclipse.core.reflection.system.rules](https://bitbucket.org/axelclk/symja_android_library/src/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/rules/)

A new function name should be added to `org.matheclipse.core.convert.AST2Expr#FUNCTION_STRINGS` `String[]` array, with a leading upper case character in its name.




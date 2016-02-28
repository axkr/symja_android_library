## How to evaluate functions ==

The Symja constants are implemented in the package:

* [https://bitbucket.org/axelclk/symja_android_library/src/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/constant/](org.matheclipse.core.builtin.constant)

The Symja functions are implemented in the packages:

* [https://bitbucket.org/axelclk/symja_android_library/src/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/function/](org.matheclipse.core.builtin.function)
* [https://bitbucket.org/axelclk/symja_android_library/src/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/](org.matheclipse.core.reflection.system)

Builtin functions must implement the interface `ICoreFunctionEvaluator` and evaluate the arguments in a non standard way.

System functions must implement the interface `IFunctionEvaluator` and are evaluated according to the assigned function symbol attributes defined in the `IFunctionEvaluator#setUp()` method.
A lot of system functions have associated pattern matching rule files in the folder:

* [https://bitbucket.org/axelclk/symja_android_library/src/master/symja_android_library/rules/](symja_android_library/rules)

These rules are converted by the class [https://bitbucket.org/axelclk/symjaunittests/src/master/SymjaUnitTests/src/org/matheclipse/core/preprocessor/RulePreprocessor.java](org.matheclipse.core.preprocessor.RulePreprocessor)
into the package [https://bitbucket.org/axelclk/symja_android_library/src/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/rules/](org.matheclipse.core.reflection.system.rules)

A new function name should be added to `org.matheclipse.core.convert.AST2Expr#FUNCTION_STRINGS` `String[]` array, with a leading upper case character in its name.




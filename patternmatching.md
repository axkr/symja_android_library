# How to use pattern-matching

## Overview

If you want to manipulate `IExpr` objects in Symja the dedicated design pattern is the visitor. 

The visitor pattern is implemented in this package:
* [org.matheclipse.core.visit](https://github.com/axkr/symja_android_library/tree/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/visit)

To simplify the manipulation of `IExpr` objects a pattern matching mechanism has been designed in the [Matcher](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/patternmatching/Matcher.java)
and [Tester](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/patternmatching/Tester.java) classes.

```
import static org.matheclipse.core.expression.F.*;
import org.matheclipse.core.patternmatching.Matcher;

  Matcher matcher = new Matcher();
  matcher.caseOf(Sin(x_), D(Sin(x), x));
  
  
  // print Cos(y)
  System.out.println(matcher.apply(Sin(y)));
```

A more advanced usage of the `Matcher` class can be found in the `TrigToExp()` function:
* [org.matheclipse.core.reflection.system.TrigToExp](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/TrigToExp.java)




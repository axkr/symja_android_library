## FullSimplify 

```
FullSimplify(expr)
```

> works like `Simplify` but additionally tries some `FunctionExpand` rule transformations to simplify `expr`.

```
FullSimplify(expr, option1, option2, ...)
```

> full simplifies `expr` with some additional options set

* Assumptions - use assumptions to simplify the expression
* ComplexFunction - use this function to determine the "weight" of an expression.

### Examples

```
>> FullSimplify(Cos(n*ArcCos(x)) == ChebyshevT(n, x))
True
```

### Related terms 
[Simplify](Simplify.md) 






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of FullSimplify](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/SimplifyFunctions.java#L1437) 

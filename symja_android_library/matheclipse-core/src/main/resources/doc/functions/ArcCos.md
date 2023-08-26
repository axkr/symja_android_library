## ArcCos

```
ArcCos(expr)
```

> returns the arc cosine (inverse cosine) of `expr` (measured in radians).
 
`ArcCos(expr)` will evaluate automatically in the cases `Infinity, -Infinity, 0, 1, -1`.

See:
* [Wikipedia - Inverse trigonometric functions](https://en.wikipedia.org/wiki/Inverse_trigonometric_functions)

### Examples

```
>> ArcCos(0)
Pi/2
 
>> ArcCos(1)
0

>> Integrate(ArcCos(x), {x, -1, 1})
Pi
```
    






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of ArcCos](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ExpTrigsFunctions.java#L212) 

* [Rule definitions of ArcCos](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/rules/ArcCosRules.m) 

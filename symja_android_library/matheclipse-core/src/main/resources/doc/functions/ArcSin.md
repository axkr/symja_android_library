## ArcSin

```
ArcSin(expr)
```

> returns the arc sine (inverse sine) of `expr` (measured in radians).
 
`ArcSin(expr)` will evaluate automatically in the cases `Infinity, -Infinity, 0, 1, -1`.

See:
* [Wikipedia - Inverse trigonometric functions](https://en.wikipedia.org/wiki/Inverse_trigonometric_functions)

### Examples

```
>> ArcSin(0)
0
 
>> ArcSin(1)
Pi/2
```
  






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of ArcSin](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ExpTrigsFunctions.java#L777) 

* [Rule definitions of ArcSin](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/rules/ArcSinRules.m) 

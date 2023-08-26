## Sin

```
Sin(expr)
```

> returns the sine of `expr` (measured in radians).
 
`Sin(expr)` will evaluate automatically in the case `expr` is a multiple of `Pi, Pi/2, Pi/3, Pi/4` and `Pi/6`.

See
* [Wikipedia - Sine](https://en.wikipedia.org/wiki/Sine)
* [Fungrim - Sine](http://fungrim.org/topic/Sine/)
* [Wikipedia - Radian](https://en.wikipedia.org/wiki/Radian)
* [Wikipedia - Degree](https://en.wikipedia.org/wiki/Degree_(angle))

### Examples

```
>> Sin(0)
0

>> Sin(0.5)
0.479425538604203

>> Sin(3*Pi)
0

>> Sin(1.0 + I)
1.2984575814159773+I*0.6349639147847361
```
 






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Sin](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ExpTrigsFunctions.java#L2874) 

* [Rule definitions of Sin](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/rules/SinRules.m) 

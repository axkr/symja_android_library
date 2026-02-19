## FindGeneratingFunction

```
FindGeneratingFunction({i1, i2, i3, ...}, var)
```

> searches for a unary generating function applied to the variable `var`, where the series coefficients equals `{i1, i2, i3, ...}`. 

**Note:** The algorithm of this function currently only calculates the Pade approximant to find a generating function.
  

See
* [Wikipedia - Generating function](https://en.wikipedia.org/wiki/Generating_function)
* [Wikipedia - Pade approximant](https://en.wikipedia.org/wiki/Pad%C3%A9_approximant)

### Examples

```
>> FindGeneratingFunction({1, 2, 3, 4}, x)
1/(1-2*x+x^2)
```

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of FindGeneratingFunction](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/FindGeneratingFunction.java#L12) 

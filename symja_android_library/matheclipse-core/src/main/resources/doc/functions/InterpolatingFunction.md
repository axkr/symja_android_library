## InterpolatingFunction

```
ipf = Interpolation(data-list)
```

> `ipf` is an `InterpolatingFunction`: the representation of the given `data-list` as piecewise `InterpolatingPolynomial`s. `ipf(x)` gives its value at `x`.

`InterpolatingFunction` objects are returned by `Interpolation`, `ListInterpolation` and `NDSolve`. Calling `InterpolatingFunction(data-list)` directly builds no function: it stays unevaluated.
 
See:  
* [Wikipedia - Newton Polynomial](https://en.wikipedia.org/wiki/Newton_polynomial) 

### Examples

```
>> ipf=Interpolation({{0, 17}, {1, 3}, {2, 5}, {3, 4}, {4, 3}, {5, 0}, {6,23}}); ipf(19/4)
-19/32
```

### Related terms 
[Interpolation](Interpolation.md), [InterpolatingPolynomial](InterpolatingPolynomial.md), [ListInterpolation](ListInterpolation.md), [NDSolve](NDSolve.md) 






### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of InterpolatingFunction](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/InterpolatingFunction.java#L15) 

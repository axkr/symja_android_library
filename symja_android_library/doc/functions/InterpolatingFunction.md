## InterpolatingFunction

```
InterpolatingFunction(data-list)
```

> get the representation for the given `data-list` as piecewise `InterpolatingPolynomial`s.
 
See:  
* [Wikipedia - Newton Polynomial](https://en.wikipedia.org/wiki/Newton_polynomial) 

### Examples

```
>> ipf=InterpolatingFunction({{0, 17}, {1, 3}, {2, 5}, {3, 4}, {4, 3}, {5, 0}, {6,23}})
InterpolatingFunction({{0,17},{1,3},{2,5},{3,4},{4,3},{5,0},{6,23}})

>> ipf(19/4)
-19/32
```

### Related terms 
[InterpolatingPolynomial](InterpolatingPolynomial.md), [NDSolve](NDSolve.md) 






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of InterpolatingFunction](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/InterpolatingFunction.java#L13) 

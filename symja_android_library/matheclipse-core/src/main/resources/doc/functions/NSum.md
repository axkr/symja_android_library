## NSum

```
NSum(expr, {i, imin, imax})
```

> evaluates the numerical approximated sum of `expr` with `i` ranging from `imin` to `imax`.
      
```
NSum(expr, {i, imin, imax, di})
```

> `i` ranges from `imin` to `imax` in steps `di`

See
* [Wikipedia - Series (mathematics)](https://en.wikipedia.org/wiki/Series_(mathematics))
* [Wikipedia - Summation](https://en.wikipedia.org/wiki/Summation)
* [Wikipedia - Convergence_tests](https://en.wikipedia.org/wiki/Convergence_tests)

### Examples

```
>> NSum(k, {k, 1, 10})    
55    
```

### Implementation status

* &#x1F9EA; - experimental

### Github

* [Implementation of NSum](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/NSum.java#L21) 

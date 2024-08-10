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

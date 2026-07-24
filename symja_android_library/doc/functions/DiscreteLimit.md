## DiscreteLimit

```
DiscreteLimit(f, n -> Infinity)
```

> `DiscreteLimit` computes the limit of the sequence `f` as the **integer** variable `n` tends to infinity.

```
DiscreteLimit(f, {k1 -> Infinity, ..., kn -> Infinity})
DiscreteLimit(f, {k1, ..., kn} -> {Infinity, ..., Infinity})
```

> `DiscreteLimit` computes the multivariate limit as an iterated limit in the given order.

Only `Infinity` and `-Infinity` are valid limit points; every other limit point is rejected with a
message and the expression stays unevaluated.

Because the limit variable runs through the integers, `DiscreteLimit` can resolve sequences for
which the continuous [Limit](Limit.md) has to answer `Indeterminate`, e.g. `Sin(Pi*n)`.

### Examples

``` 
>> DiscreteLimit(n / (n + 1), n -> Infinity) 
1

>> DiscreteLimit((3 * n^2) / (n^2 + 5), n -> Infinity) 
3

>> DiscreteLimit(E^n, n -> -Infinity) 
0

>> DiscreteLimit((1 + 1/n)^n, n -> Infinity) 
E

>> DiscreteLimit((-1)^n / n, n -> Infinity) 
0

>> DiscreteLimit(Sin(Pi*n), n -> Infinity) 
0

>> DiscreteLimit((-1)^n, n -> Infinity) 
Indeterminate

>> DiscreteLimit((n / (n + 2)) * E^(-m / (m + 1)), {m -> Infinity, n -> Infinity}) 
1/E
```

### Related terms 
[DiscreteRatio](DiscreteRatio.md), [DiscreteShift](DiscreteShift.md), [Limit](Limit.md)
 
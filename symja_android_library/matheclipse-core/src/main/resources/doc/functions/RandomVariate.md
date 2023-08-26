## RandomVariate

```
RandomVariate(distribution)
```

> create a pseudo random variate from the `distribution`.

```
RandomVariate(distribution, number-of-variates)
```

> create `number-of-variates` pseudo random variates from the `distribution`.
 
```
RandomVariate(distribution, {n1, n2,...})
```

> create a `{n1, n2,...}` tensor of pseudo random variates.

### Examples

The random variates of a binomial distribution can be generated with function `RandomVariate`.

```
>> RandomVariate(BinomialDistribution(10,0.25), 10^1)
{1,2,1,1,4,1,1,3,2,5}
```
 
```
>> RandomVariate(NormalDistribution(2,3),{2,3,4})
{{{4.62132,1.44626,-2.01995,4.19106},{1.84104,-2.36264,5.36121,-1.24045},{1.99449,2.59837,4.11966,4.19874}},{{1.46834,1.7775,3.00797,-2.44187},{1.54419,1.36861,4.29747,1.21262},{5.95268,1.52483,-4.27536,1.29105}}}
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of RandomVariate](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/StatisticsFunctions.java#L6373) 

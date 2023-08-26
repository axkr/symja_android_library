## InverseCDF

```
InverseCDF(dist, q)
```

> returns the inverse cumulative distribution for the distribution `dist` as a function of `q` 


```
InverseCDF(dist, {x1, x2, ...})
```

> returns the inverse CDF evaluated at `{x1, x2, ...}` 

```
InverseCDF(dist)
```

> returns the inverse CDF as a pure function.


### Examples

 
```   
>> InverseCDF[NormalDistribution[0, 1], x]
ConditionalExpression(-Sqrt(2)*InverseErfc(2*#1),0<=#1<=1)&
```

### Related terms 
[CDF](CDF.md), [PDF](PDF.md)  






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of InverseCDF](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/StatisticsFunctions.java#L233) 

## SurvivalFunction

```
SurvivalFunction(dist, x)
```

> returns the survival function for the distribution `dist` evaluated at `x`. 

```
SurvivalFunction(dist, {x1, x2, ...})
```

> returns the survival function at `{x1, x2, ...}`.

```
SurvivalFunction(dist)
```

> returns the survival function as a pure function. 

See
* [Wikipedia - Survival function](https://en.wikipedia.org/wiki/Survival_function)
 
### Examples

```
>> SurvivalFunction(NormalDistribution(0, 1), x)
1-Erfc(-x/Sqrt(2))/2
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of SurvivalFunction](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/StatisticsFunctions.java#L6944) 

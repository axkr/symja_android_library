## Expectation

```
Expectation(pure-function, data-set)
```

> returns the expected value of the `pure-function` for the given `data-set`. 
   

See
* [Wikipedia - Expected value](https://en.wikipedia.org/wiki/Expected_value)

### Examples

```
>> Expectation((#^3)&, {a,b,c}) 
1/3*(a^3+b^3+c^3) 

>> Expectation(2*x+3,Distributed(x,{a,b,c,d})) 
1/4*(12+2*a+2*b+2*c+2*d) 
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Expectation](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/StatisticsFunctions.java#L3781) 

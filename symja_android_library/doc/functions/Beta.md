## Beta

```
Beta(a, b) 
```

> is the beta function of the numbers `a`,`b`.

```
Beta(x, a, b) 
```

> The incomplete beta function is a generalization of the beta function. For `x = 1`, the incomplete beta function coincides with the complete beta function.  

See
* [Wikipedia - Beta function](https://en.wikipedia.org/wiki/Beta_function)
* [Fungrim - Beta function](http://fungrim.org/topic/Beta_function/)


### Examples

```
>> Beta(2.3, 3.2)
0.0540298

>> Beta(2, 3)
1/12

>> 12*Beta(1.0, 2, 3)
1.0
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Beta](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/SpecialFunctions.java#L116) 

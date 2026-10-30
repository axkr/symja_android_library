## Hypergeometric1F1

```
Hypergeometric1F1(a, b, z)
```

> return the `Hypergeometric1F1` function

See:
* [Wikipedia - Confluent hypergeometric function](https://en.wikipedia.org/wiki/Confluent_hypergeometric_function)
* [Wikipedia - Generalized hypergeometric function](https://en.wikipedia.org/wiki/Generalized_hypergeometric_function)
* [Fungrim - Confluent hypergeometric functions](http://fungrim.org/topic/Confluent_hypergeometric_functions/)

### Examples

```
>> Hypergeometric1F1(1, 2, 0.5)
1.29744

>> Hypergeometric1F1(1, 2, x)
(-1+E^x)/x
```

For a negative integer `a` the series ends, and the result is a polynomial:

```
>> Hypergeometric1F1(-2, 1, x)
1-2*x+x^2/2
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Hypergeometric1F1](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/HypergeometricFunctions.java#L1058) 

* [Rule definitions of Hypergeometric1F1](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/rules/Hypergeometric1F1Rules.m) 

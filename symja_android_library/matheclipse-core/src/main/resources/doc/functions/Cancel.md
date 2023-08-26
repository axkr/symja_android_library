## Cancel

```
Cancel(expr)
```

> cancels out common factors in numerators and denominators.

### Examples

```
>> Cancel(x / x ^ 2)
1/x
```

`Cancel` threads over sums:

```
>> Cancel(x / x ^ 2 + y / y ^ 2)
1/x+1/y
 
>> Cancel(f(x) / x + x * f(x) / x ^ 2)
(2*f(x))/x
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Cancel](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Algebra.java#L483) 

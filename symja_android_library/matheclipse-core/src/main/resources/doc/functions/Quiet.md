## Quiet

```
Quiet(expr)
```

> evaluates `expr` in "quiet" mode (i.e. no warning messages are shown during evaluation).

### Examples
 
No error is printed for the division by `0`

```
>> Quiet(1/0) 
ComplexInfinity
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Quiet](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Programming.java#L2540) 

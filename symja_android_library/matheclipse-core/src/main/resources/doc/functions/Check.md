## Check

```
Check(expr, failure)
```

> evaluates `expr`, and returns the result, unless messages were generated, in which case  `failure` will be returned.

### Examples
 
```
>> Check(2^(3), err)
8
```

`0^(-42)` prints message: "Power: Infinite expression 1/0^42 encountered."

```
>> Check(0^(-42), failure)
failure
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Check](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Programming.java#L361) 

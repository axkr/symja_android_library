## TimeConstrained

```
TimeConstrained(expression, seconds)
```

> stop evaluation of `expression` if time measurement of the evaluation exceeds `seconds` and return `$Aborted`.
 
```
TimeConstrained(expression, seconds, default)
```

> return `default` instead of `$Aborted` if the evaluation exceeds `seconds`.

### Examples

```    
>> TimeConstrained(Pause(5), 2)
$Aborted

>> TimeConstrained(Pause(1); "Hello World", 10)
Hello World

>> TimeConstrained(Pause(5), 2, "test")
test
```

### Related terms 
[Pause](Pause.md), [Timing](Timing.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of TimeConstrained](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Programming.java#L3033) 

## TakeLargestBy

```
TakeLargestBy({e_1, e_2, ..., e_i}, function, n) 
```

> returns the `n` values from the list `{e_1, e_2, ..., e_i}`, where `function(e_i)` is largest.

### Examples

```
>> TakeLargestBy(Prime(Range(10)),Mod(#,7)&, 3) 
{13,5,19}
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of TakeLargestBy](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ListFunctions.java#L7579) 

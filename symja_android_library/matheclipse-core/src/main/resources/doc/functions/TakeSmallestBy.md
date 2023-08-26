## TakeSmallestBy

```
TakeSmallestBy({e_1, e_2, ..., e_i}, function, n) 
```

> returns the `n` values from the list `{e_1, e_2, ..., e_i}`, where `function(e_i)` is smallest.

### Examples

```
>> TakeSmallestBy(Prime(Range(10)),Mod(#,7)&, 3) 
{7,29,2}
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of TakeSmallestBy](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ListFunctions.java#L7687) 

## Break

``` 
Break()
``` 
> exits a `For`, `While`, or `Do` loop.

### Examples
``` 
>> n = 0
>> While(True, If(n>10, Break()); n=n+1)
>> n
11
```

### Related terms 
[Continue](Continue.md), [Do](Do.md), [For](For.md), [While](While.md) 






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Break](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Programming.java#L214) 

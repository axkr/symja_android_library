## Context 

```
Context(symbol)
```

> yields the name of the context where `symbol` is defined in.


```
Context()
```

> return the current context.

### Examples

``` 
>> $ContextPath
{System`,Global`}

>> Context(a)
Global`

>> Context(Sin)
System`
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Context](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/PatternMatching.java#L333) 

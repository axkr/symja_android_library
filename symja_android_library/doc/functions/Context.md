## Context 

```
Context(symbol)
```

> return the context of the given symbol.


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

### Github

* [Implementation of Context](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/PatternMatching.java#L321) 

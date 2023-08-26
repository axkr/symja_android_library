## Nothing

```
Nothing
```

> during evaluation of a list with a `Nothing` element `{..., Nothing, ...}`, the symbol `Nothing` is removed from the arguments.

```
Nothing(...)
```
> an function with head `Nothing` will be reduced to the symbol `Nothing`.

### Examples

```
>> {a, Nothing, Nothing, d}
{a,d}

>> bar(a, Nothing, baz(Nothing), c)
bar(a,Nothing,baz(Nothing),c)
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Nothing](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/PatternMatching.java#L1092) 

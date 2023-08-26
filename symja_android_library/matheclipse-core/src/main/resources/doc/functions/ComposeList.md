## ComposeList

```
ComposeList(list-of-symbols, variable)
```

> creates a list of compositions of the symbols applied at the argument `x`.

### Examples

```
>> ComposeList({f,g,h}, x)
{x,f(x),g(f(x)),h(g(f(x)))}

```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of ComposeList](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ListFunctions.java#L1722) 

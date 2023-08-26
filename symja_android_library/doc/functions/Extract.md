## Extract

```
Extract(expr, list)
```

> extracts parts of `expr` specified by `list`.

```
Extract(expr, {list1, list2, ...})'
```

> extracts a list of parts.

### Examples

`Extract(expr, i, j, ...)` is equivalent to `Part(expr, {i, j, ...})`.

```
>> Extract(a + b + c, {2})
b
 
>> Extract({{a, b}, {c, d}}, {{1}, {2, 2}})
{{a,b},d}
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Extract](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ListFunctions.java#L2683) 

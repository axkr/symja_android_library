## SortBy

```
SortBy(list, f) 
```

> sorts `list` (or the elements of any other expression) according to canonical ordering of the keys that are extracted from the `list`'s elements using `f`. Chunks of leaves that appear the same under `f` are sorted according to their natural order (without applying `f`).
 
```
Sort(f)
```

> creates an operator function that, when applied, sorts by `f`.
 
### Examples
    
```
>> SortBy({{5, 1}, {10, -1}}, Last)
{{10,-1},{5,1}}

>> SortBy(Total)[{{5, 1}, {10, -9}}] 
{{10,-9},{5,1}}
```







### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of SortBy](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/StructureFunctions.java#L1985) 

## FindCycle

```
FindCycle(graph)
```
 
> Find a cycle in the given `graph`.
 
### Examples

```
>> FindCycle({2 -> 1, 1 -> 4, 3 -> 2, 2 -> 5, 6 -> 3, 5 -> 4, 4 -> 7, 6 -> 5, 8 -> 5, 6 -> 9, 7 -> 8, 7 -> 10, 9 -> 8, 11 -> 8, 12 -> 9, 10 -> 11, 12 -> 11}, {6,6}, All)
{{8->5,5->4,4->7,7->10,10->11,11->8}}
```

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of FindCycle](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/GraphFunctions.java#L1721) 

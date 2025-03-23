## PathGraph

```
PathGraph({vertex1, vertex2, ...})
```

> create a new path graph with the given vertices `vertex1, vertex2, ...`.
 
See
* [Wikipedia - Path graph](https://en.wikipedia.org/wiki/Path_graph) 

### Examples

```
>> PathGraph({1,2,3,4}) // AdjacencyMatrix // Normal
{{0,1,0,0},
 {1,0,1,0},
 {0,1,0,1},
 {0,0,1,0}}
```

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of PathGraph](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/GraphDataFunctions.java#L335) 

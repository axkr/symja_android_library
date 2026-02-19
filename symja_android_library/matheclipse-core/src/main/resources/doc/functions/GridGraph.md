## GridGraph

```
GridGraph({v1,v2})
```

> returns the grid graph with `v1 x v2` vertices.
 
See
* [Wikipedia - Lattice graph](https://en.wikipedia.org/wiki/Lattice_graph) 

### Examples

```
>> GridGraph({3,4}) // AdjacencyMatrix // MatrixForm
{{0,1,0,1,0,0,0,0,0,0,0,0},
 {1,0,1,0,1,0,0,0,0,0,0,0},
 {0,1,0,0,0,1,0,0,0,0,0,0},
 {1,0,0,0,1,0,1,0,0,0,0,0},
 {0,1,0,1,0,1,0,1,0,0,0,0},
 {0,0,1,0,1,0,0,0,1,0,0,0},
 {0,0,0,1,0,0,0,1,0,1,0,0},
 {0,0,0,0,1,0,1,0,1,0,1,0},
 {0,0,0,0,0,1,0,1,0,0,0,1},
 {0,0,0,0,0,0,1,0,0,0,1,0},
 {0,0,0,0,0,0,0,1,0,1,0,1},
 {0,0,0,0,0,0,0,0,1,0,1,0}}
```

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of GridGraph](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/GraphDataFunctions.java#L265) 

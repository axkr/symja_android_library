## HyperCubeGraph

```
HyperCubeGraph(order)
```

> the hypercube graph `Q_n` is the graph formed from the vertices and edges of an n-dimensional hypercube. For instance, the cube graph `Q_3` is the graph formed by the 8 vertices and 12 edges of a three-dimensional cube.
 
See
* [Wikipedia - Hypercube graph](https://en.wikipedia.org/wiki/Hypercube_graph) 

### Examples

```
>> HypercubeGraph(4) // AdjacencyMatrix // Normal
{{0,1,1,0,1,0,0,0,1,0,0,0,0,0,0,0},
 {1,0,0,1,0,1,0,0,0,1,0,0,0,0,0,0},
 {1,0,0,1,0,0,1,0,0,0,1,0,0,0,0,0},
 {0,1,1,0,0,0,0,1,0,0,0,1,0,0,0,0},
 {1,0,0,0,0,1,1,0,0,0,0,0,1,0,0,0},
 {0,1,0,0,1,0,0,1,0,0,0,0,0,1,0,0},
 {0,0,1,0,1,0,0,1,0,0,0,0,0,0,1,0},
 {0,0,0,1,0,1,1,0,0,0,0,0,0,0,0,1},
 {1,0,0,0,0,0,0,0,0,1,1,0,1,0,0,0},
 {0,1,0,0,0,0,0,0,1,0,0,1,0,1,0,0},
 {0,0,1,0,0,0,0,0,1,0,0,1,0,0,1,0},
 {0,0,0,1,0,0,0,0,0,1,1,0,0,0,0,1},
 {0,0,0,0,1,0,0,0,1,0,0,0,0,1,1,0},
 {0,0,0,0,0,1,0,0,0,1,0,0,1,0,0,1},
 {0,0,0,0,0,0,1,0,0,0,1,0,1,0,0,1},
 {0,0,0,0,0,0,0,1,0,0,0,1,0,1,1,0}}
```

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of HypercubeGraph](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/GraphDataFunctions.java#L296) 

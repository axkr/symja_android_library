## StarGraph

```
StarGraph(order)
```

> create a new star graph with `order` number of total vertices including the center vertex.
 
See
* [Wikipedia - Star (graph theory)](https://en.wikipedia.org/wiki/Star_(graph_theory)) 

### Examples

```
>> StarGraph(4) // AdjacencyMatrix // Normal 
{{0,1,1,1},
 {1,0,0,0}, 
 {1,0,0,0}, 
 {1,0,0,0}} 

>> StarGraph(6)
Graph({1,2,3,4,5,6},{2<->1,3<->1,4<->1,5<->1,6<->1})
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of StarGraph](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/GraphDataFunctions.java#L387) 

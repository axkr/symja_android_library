## PetersenGraph

```
PetersenGraph()
```

> create a `PetersenGraph(5, 2)` graph.

```
PetersenGraph(order, k)
```

> create a new Petersen graph with `2*order` number of total vertices.

See
* [Wikipedia - Petersen graph](https://en.wikipedia.org/wiki/Petersen_graph) 

### Examples

```
>> PetersenGraph()
Graph({1,2,3,4,5,6,7,8,9,10},{1<->3,1<->2,2<->6,3<->5,3<->4,4<->8,5<->7,5<->6,6<->10,7<->9,7<->8,8<->2,9<->1,9<->10,10<->4})
```







### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of PetersenGraph](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/GraphDataFunctions.java#L266) 

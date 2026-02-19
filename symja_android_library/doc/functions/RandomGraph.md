## RandomGraph

```
RandomGraph({number-of-vertices,number-of-edges})
```

> create a random graph with `number-of-vertices` vertices and `number-of-edges` edges. 

See
* [Wikipedia - Random graph](https://en.wikipedia.org/wiki/Random_graph) 


### Examples

```
>> RandomGraph({5,10})
Graph({1,2,3,4,5},{5<->3,4<->2,5<->1,2<->3,5<->4,2<->5,2<->1,3<->4,3<->1,1<->4})
```






### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of RandomGraph](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/GraphDataFunctions.java#L461) 

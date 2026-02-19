## ClosenessCentrality

```
ClosenessCentrality(graph)
```

> Computes the closeness centrality of each vertex of a `graph`.

See
* [Wikipedia - Closeness centrality](https://en.wikipedia.org/wiki/Closeness_centrality)
* [Youtube - Betweenness centrality](https://youtu.be/0CCrq62TF7U)

### Examples

```
>> ClosenessCentrality(Graph({1, 2, 3, 4, 5},{1<->2,1<->3,2<->3,3<->4,3<->5}))
{0.666667,0.666667,1.0,0.571429,0.571429}
```

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of ClosenessCentrality](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/GraphFunctions.java#L1404) 

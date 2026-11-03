## LambdaComponents

```
LambdaComponents(graph)
```

> returns the lambda components of `graph`. A lambda component is a set of vertices which are joined to each other by more edge-independent paths than to any vertex outside of the set.

```
LambdaComponents(graph, {v1, v2, ...})
```

> returns only the lambda components which contain at least one of the vertices `v1, v2, ...`.

The number of edge-independent paths between two vertices is the smallest number of edges whose removal separates them. Lambda components do not overlap partially: two of them are either disjoint, or one contains the other, so a vertex can belong to several lambda components of different sizes.

See
* [Wikipedia - Connectivity (graph theory)](https://en.wikipedia.org/wiki/Connectivity_(graph_theory))

### Examples

Two triangles joined by the bridge `3<->4`:

```
>> LambdaComponents({1<->2,2<->3,3<->1,3<->4,4<->5,5<->6,6<->4})
{{1,2,3,4,5,6},{1,2,3},{4,5,6}}

>> LambdaComponents({1<->2,2<->3,3<->1,3<->4,4<->5,5<->6,6<->4}, {1})
{{1,2,3,4,5,6},{1,2,3}}
```

```
>> LambdaComponents(CompleteGraph(4))
{{1,2,3,4}}
```

### Related terms
[ConnectedGraphQ](ConnectedGraphQ.md), [Graph](Graph.md), [KCoreComponents](KCoreComponents.md), [LuccioSamiComponents](LuccioSamiComponents.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of LambdaComponents](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-graphtheory/src/main/java/org/matheclipse/graphtheory/reflection/LambdaComponents.java)

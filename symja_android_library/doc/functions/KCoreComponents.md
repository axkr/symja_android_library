## KCoreComponents

```
KCoreComponents(graph, k)
```

> returns the k-core components of `graph`: the maximal connected subgraphs in which every vertex has a degree of at least `k`. For a directed graph the components are weakly connected. Each component is given as a list of vertices.

```
KCoreComponents(graph, k, "In")
```

> counts only the incoming edges of a directed `graph`.

```
KCoreComponents(graph, k, "Out")
```

> counts only the outgoing edges of a directed `graph`.

The k-core is found by removing the vertices with a degree below `k` again and again, because removing a vertex lowers the degree of its neighbors. If `graph` has no k-core, the result is the empty list `{}`.

See
* [Wikipedia - Degeneracy (graph theory)](https://en.wikipedia.org/wiki/Degeneracy_(graph_theory))

### Examples

The vertices `4` and `5` are removed, the triangle remains:

```
>> KCoreComponents({1<->2,2<->3,3<->1,3<->4,4<->5}, 2)
{{1,2,3}}
```

Every vertex of the Petersen graph has the degree `3`:

```
>> KCoreComponents(PetersenGraph(), 3)
{{1,2,3,4,5,6,7,8,9,10}}

>> KCoreComponents(PetersenGraph(), 4)
{}
```

The vertex `4` has an incoming, but no outgoing edge:

```
>> KCoreComponents(Graph({1->2,2->3,3->1,3->4}), 1, "In")
{{1,2,3,4}}

>> KCoreComponents(Graph({1->2,2->3,3->1,3->4}), 1, "Out")
{{1,2,3}}
```

### Related terms
[Graph](Graph.md), [LambdaComponents](LambdaComponents.md), [LuccioSamiComponents](LuccioSamiComponents.md), [WeaklyConnectedComponents](WeaklyConnectedComponents.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of KCoreComponents](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-graphtheory/src/main/java/org/matheclipse/graphtheory/reflection/KCoreComponents.java)

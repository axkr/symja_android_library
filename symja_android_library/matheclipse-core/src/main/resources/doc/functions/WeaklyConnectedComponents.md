## WeaklyConnectedComponents

```
WeaklyConnectedComponents(graph)
```

> gives the weakly connected components of `graph`: the connected components when the direction of the edges is ignored.

```
WeaklyConnectedComponents(graph, {v1, v2, ...})
```

> gives the weakly connected components which contain one of the vertices `v1, v2, ...`.

See
* [Wikipedia - Connectivity (graph theory)](https://en.wikipedia.org/wiki/Connectivity_(graph_theory))

### Examples

```
>> WeaklyConnectedComponents(Graph({1->2,3->4,4->3,5->5}))
{{2,1},{4,3},{5}}

>> WeaklyConnectedComponents(Graph({1->2,3->4}),{3})
{{4,3}}
```

### Related terms
[ConnectedComponents](ConnectedComponents.md), [ConnectedGraphComponents](ConnectedGraphComponents.md), [VertexConnectivity](VertexConnectivity.md)

### Implementation status

* &#x2611; - partially implemented

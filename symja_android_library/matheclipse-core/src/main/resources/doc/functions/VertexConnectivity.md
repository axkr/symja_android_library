## VertexConnectivity

```
VertexConnectivity(graph)
```

> gives the smallest number of vertices whose removal disconnects `graph`.

```
VertexConnectivity(graph, s, t)
```

> gives the smallest number of vertices whose removal disconnects the vertex `t` from the vertex `s`.

See
* [Wikipedia - Connectivity (graph theory)](https://en.wikipedia.org/wiki/Connectivity_(graph_theory))
* [Wikipedia - Menger's theorem](https://en.wikipedia.org/wiki/Menger%27s_theorem)

### Examples

```
>> VertexConnectivity(PetersenGraph())
3

>> VertexConnectivity(CompleteGraph(4))
3

>> VertexConnectivity(CycleGraph(5), 1, 3)
2
```

### Related terms
[WeaklyConnectedComponents](WeaklyConnectedComponents.md)

### Implementation status

* &#x2611; - partially implemented

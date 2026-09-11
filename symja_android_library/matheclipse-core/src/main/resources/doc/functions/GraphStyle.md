## GraphStyle

```
Graph({v1, v2, ...}, {e1, e2, ...}, GraphStyle -> "name")
```

> is an option of `Graph` which draws the graph in a named style.

The styles `"SmallNetwork"`, `"DiagramGreen"` and `"VintageDiagram"` draw every vertex as a rectangle with its name on it, in the colours Mathematica uses for them. Other names are ignored. Options given with the graph, like `VertexStyle` or `EdgeStyle`, still change a part of the style.

### Examples

```
>> Graph({1, 2, 3, 4, 5, 6}, {1<->2, 1<->6, 2<->3, 3<->4, 4<->5, 5<->6}, {GraphLayout -> "CircularEmbedding", GraphStyle -> "SmallNetwork"})
```

### Related terms
[Graph](Graph.md), [GraphPlot](GraphPlot.md), [HighlightGraph](HighlightGraph.md)

### Implementation status

* &#x1F9EA; - experimental

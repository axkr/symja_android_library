## HighlightGraph

```
HighlightGraph(graph, {v1, e1, Style(v2, color), ...})
```

> returns the `graph` with the given vertices and edges drawn in their own style: in the colour of `Style(item, color)`, or red.

### Examples

```
>> HighlightGraph(PathGraph(Range(5)), {Style(2, Green), 3, 1<->2})
```

Colour every vertex by its closeness centrality:

```
>> g = PathGraph(Range(25));
>> HighlightGraph(g, Table(Style(VertexList(g)[[i]], ColorData("TemperatureMap")(ClosenessCentrality(g)[[i]] / Max(ClosenessCentrality(g)))), {i, VertexCount(g)}))
```

### Related terms
[Graph](Graph.md), [GraphPlot](GraphPlot.md), [GraphStyle](GraphStyle.md)

### Implementation status

* &#x1F9EA; - experimental

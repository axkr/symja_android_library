## ButterflyGraph

```
ButterflyGraph(n)
```

> returns the butterfly graph of `n` levels, with `(n+1)*2^n` vertices and `2*n*2^n` edges.

The vertex of level `i` (from `0` to `n`) and `n`-bit word `w` is joined to the vertices of level `i+1` with the same word and with bit `i` of the word flipped. The vertices are numbered level by level, starting with `1`.

See
* [Wikipedia - Butterfly network](https://en.wikipedia.org/wiki/Butterfly_network)

### Examples

```
>> {VertexCount(ButterflyGraph(3)), EdgeCount(ButterflyGraph(3))}
{32,48}

>> Graph3D(ButterflyGraph(3))
```

### Related terms
[Graph](Graph.md), [Graph3D](Graph3D.md), [HypercubeGraph](HypercubeGraph.md)

### Implementation status

* &#x1F9EA; - experimental

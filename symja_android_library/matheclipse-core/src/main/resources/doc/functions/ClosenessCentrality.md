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

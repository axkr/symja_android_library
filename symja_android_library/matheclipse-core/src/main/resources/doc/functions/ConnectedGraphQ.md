## ConnectedGraphQ

```
ConnectedGraphQ(graph)
```

>  returns `True` if the `graph` is strongly connected, which means that every vertex is reachable from every other vertex.
 
See
* [Wikipedia - Strongly connected component](https://en.wikipedia.org/wiki/Strongly_connected_component) 

### Examples

```
>> ConnectedGraphQ(Graph({1,2,3,4},{1<->2, 2<->3, 3<->4}))
True
```

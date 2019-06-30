## VertexQ

``` 
VertexQ(graph, vertex)
```

> test if `vertex` is a vertex in the `graph` object.


See:
* [Wikipedia - Graph theory](https://en.wikipedia.org/wiki/Graph_theory)
 

### Examples

```
>> VertexQ(Graph({1 -> 2, 2 -> 3, 1 -> 3, 4 -> 2}),3) 
True

>> VertexQ(Graph({1 -> 2, 2 -> 3, 1 -> 3, 4 -> 2}),5) 
False
```

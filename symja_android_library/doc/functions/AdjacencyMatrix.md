## AdjacencyMatrix

``` 
AdjacencyMatrix(graph)
```

> convert the `graph` into a adjacency matrix.

See:
* [Wikipedia - Adjacency matrix](https://en.wikipedia.org/wiki/Adjacency_matrix)

### Examples

```
>> AdjacencyMatrix(Graph({1 -> 2, 2 -> 3, 1 -> 3, 4 -> 2})) 
{{0,1,1,0}, 
 {0,0,1,0}, 
 {0,0,0,0}, 
 {0,1,0,0}}
```

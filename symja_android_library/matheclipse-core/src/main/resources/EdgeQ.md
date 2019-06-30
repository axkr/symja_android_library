## EdgeQ

``` 
EdgeQ(graph, edge)
```

> test if `edge` is an edge in the `graph` object.


See:
* [Wikipedia - Graph theory](https://en.wikipedia.org/wiki/Graph_theory)
 

### Examples

```
>> EdgeQ(Graph({1 -> 2, 2 -> 3, 1 -> 3, 4 -> 2}),2 -> 3) 
True

>> EdgeQ(Graph({1 -> 2, 2 -> 3, 1 -> 3, 4 -> 2}),2 -> 4) 
False
```

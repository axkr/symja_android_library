## Graph

``` 
Graph({edge1,...,edgeN})
```

> create a graph from the given edges `edge1,...,edgeN`.


See:
* [Wikipedia - Graph](https://en.wikipedia.org/wiki/Graph_(discrete_mathematics))
* [Wikipedia - Graph theory](https://en.wikipedia.org/wiki/Graph_theory)
 

### Examples

A directed graph:

```
>> Graph({1 -> 2, 2 -> 3, 3 -> 4, 4 -> 1})  
```

An undirected graph:

```			
>> Graph({1 <-> 2, 2 <-> 3, 3 <-> 4, 4 <-> 1})   
```

An undirected weighted graph:

```			
>> Graph({1 <-> 2, 2 <-> 3, 3 <-> 4, 4 <-> 1},{EdgeWeight->{2.0,3.0,4.0, 5.0}})   
```

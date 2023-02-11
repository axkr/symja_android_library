## PlanarGraphQ

```
PlanarGraphQ(g)
```

> Returns `True` if `g` is a planar graph and `False` otherwise.

See
* [Wikipedia - Planar graph](https://en.wikipedia.org/wiki/Planar_graph) 

### Examples

```
>> PlanarGraphQ(CycleGraph(4)) 
True

>> PlanarGraphQ(CompleteGraph(5)) 
False

>> PlanarGraphQ(CompleteGraph(4)) 
True

>> PlanarGraphQ("abc") 
False
```


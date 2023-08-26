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







### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of PlanarGraphQ](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/GraphFunctions.java#L1951) 

## TreePlot
 
```
TreePlot(graph-expr)
```

> create a tree plot from the given graph expression `graph-expr`.

See:  
* [Wikipedia - Tree structure](https://en.wikipedia.org/wiki/Tree_structure) 

### Examples 

Generate a [Graphics](Graphics.md) output to visualize the structure of the input expression:

```
>> TreePlot(KaryTree(7, 3))
-Graphics- 
```

### Implementation status

* &#x1F9EA; - experimental

### Github

* [Implementation of TreePlot](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/TreePlot.java#L21) 

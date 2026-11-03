## LuccioSamiComponents

```
LuccioSamiComponents(graph)
```

> returns the Luccio-Sami components of `graph`. A Luccio-Sami component, or LS set, is a set of vertices in which every proper subset has more edges to the rest of the set than to vertices outside of the set.

```
LuccioSamiComponents(graph, {v1, v2, ...})
```

> returns only the Luccio-Sami components which contain at least one of the vertices `v1, v2, ...`.

Every Luccio-Sami component is also a lambda component.

See
* F. Luccio, M. Sami - On the decomposition of networks in minimally interconnected subnetworks, IEEE Transactions on Circuit Theory 16 (1969)

### Examples

Two triangles joined by the bridge `3<->4`:

```
>> LuccioSamiComponents({1<->2,2<->3,3<->1,3<->4,4<->5,5<->6,6<->4})
{{1,2,3,4,5,6},{1,2,3},{4,5,6}}

>> LuccioSamiComponents({1<->2,2<->3,3<->1,3<->4,4<->5,5<->6,6<->4}, {5})
{{1,2,3,4,5,6},{4,5,6}}
```

```
>> LuccioSamiComponents(CompleteGraph(4))
{{1,2,3,4}}
```

### Related terms
[Graph](Graph.md), [KCoreComponents](KCoreComponents.md), [LambdaComponents](LambdaComponents.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of LuccioSamiComponents](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-graphtheory/src/main/java/org/matheclipse/graphtheory/reflection/LuccioSamiComponents.java)

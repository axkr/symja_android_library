## Export

```
Export("path-to-filename", expression, "WXF")
```
 
> if the file system is enabled, export the `expression` in WXF format to the "path-to-filename" file.
 

```
Export("path-to-filename", expression, "Table")
```
 
> if the file system is enabled, export the `expression` in table format to the "path-to-filename" file.

```
Export("path-to-filename", graph, "GraphML")

or

Export("path-to-filename", graph, "DOT")
```
 
> if the file system is enabled, export the `graph` in `GraphML` or `DOT` format to the "path-to-filename" file.

### Examples

Export a graph: 

```
>> Export("c:\\temp\\dotgraph.dot",Graph({1 \[DirectedEdge] 2, 2 \[DirectedEdge] 3, 3 \[DirectedEdge] 1}))
c:\\temp\\dotgraph.dot
```

### Related terms 
[BinaryDeserialize](BinaryDeserialize.md), [BinarySerialize](BinarySerialize.md), [ByteArray](ByteArray.md), [ByteArrayQ](ByteArrayQ.md), [Import](Import.md)






### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of Export](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/Export.java#L42) 

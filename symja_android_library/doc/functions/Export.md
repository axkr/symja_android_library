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

[Import](Import.md)
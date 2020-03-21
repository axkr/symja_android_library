## Import

```
Import("path-to-filename", "WXF")
```
 
> if the file system is enabled, import an expression in WXF format from the "path-to-filename" file.


``` 
Import("path-to-filename", "Table")
```
 
> if the file system is enabled, import an expression in table format from the "path-to-filename" file.


``` 
Import("path-to-filename", "GraphML")

or

Import("path-to-filename", "DOT")
```
 
> if the file system is enabled, import a graph in `GraphML` or `DOT` format from the "path-to-filename" file.


### Examples

Import a graph: 

```
>> Import("c:\\temp\\dotgraph.graphml", "GraphML") 
Graph({1, 2, 3}, {(1,2), (2,3), (3,1)})
```

### Related terms 
[BinaryDeserialize](BinaryDeserialize.md), [BinarySerialize](BinarySerialize.md), [ByteArray](ByteArray.md), [ByteArrayQ](ByteArrayQ.md), [Export](Export.md)
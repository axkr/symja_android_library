## Save

```
Save("path-to-filename", expression)
```
 
> if the file system is enabled, export the `FullDefinition` of the `expression` to the "path-to-filename" file. The saved file can be imported with `Get`.
 
```
Save("path-to-filename", "Global`*")
```
 
> if the file system is enabled, export the `FullDefinition` of all symbols in the `"Global`*"` context to the "path-to-filename" file. 


### Examples

Save a definition with dependent symbol definitions into temporary file

```
>> g(x_) := x^3;

>> g(x_,y_) := f(x,y); 

>> SetAttributes(f, Listable); 

>> f(x_) := g(x^2); 

>> temp = FileNameJoin({$TemporaryDirectory, \"savedlist.txt\"});Print(temp); 

>> Save(temp, {f,g}) 
 
>> ClearAll(f,g) 
  
>> "Attributes(f)  

>> {f(2),g(7)}

>> Get(temp) 
     
>> {f(2),g(7)} 
{64,343}

>> Attributes(f) 
{Listable}
```

### Related terms 
[BinaryDeserialize](BinaryDeserialize.md), [BinarySerialize](BinarySerialize.md), [ByteArray](ByteArray.md), [ByteArrayQ](ByteArrayQ.md), [Export](Export.md), [Import](Import.md)






### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of Export](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/Export.java#L42) 

## ReadList

```
ReadList(input-stream)
```

> reads all the expressions until the end of file and return a list of these expressions.

```
ReadList(input-stream, object-type)
```

> reads the the expressions of type `object-type` and return a list of these expressions.

### Examples

``` 
>> ReadList(StringToStream("(**)\n\n\n{0, x, 1, 0}\n{1, x, 1, x}")) 
{Null,{0,x,1,0},{1,x,1,x}} 

>> ReadList(StringToStream("(**)\n\n\n{0, x, 1, 0}\n{1, x, 1, x}"), Expression) 
{Null,{0,x,1,0},{1,x,1,x}} 

>> ReadList(StringToStream("(**)\n\n\n{0, x, 1, 0}\n{1, x, 1, x}"), Expression, 2) 
{Null,{0,x,1,0}} 

>> ReadList(StringToStream("(**)\n\n\n{0, x, 1, 0}\n{1, x, 1, x}"), String) // InputForm 
{"(**)","{0, x, 1, 0}","{1, x, 1, x}"}
```

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of ReadList](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/FileFunctions.java#L1212) 

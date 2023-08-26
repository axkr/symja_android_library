## StringToStream

```
StringToStream("string")
```

> converts a `string` to an open input stream.

### Examples

``` 
>> str = StringToStream("4711 dummy 0815")
InputStream[Name: String Unique-ID: 1]

>> Read(str, Number) 
4711

>> Read(str, {Word, Number})
{dummy,0815}

>>Close(str) 
String 
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of StringToStream](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/FileFunctions.java#L1195) 

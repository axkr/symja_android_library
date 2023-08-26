## Read

```
Read(input-stream)
```

> reads the `input-stream` and return one expression.

```
Read(input-stream, object-type)
```

> reads the `input-stream` and return one expression of type `object-type` (`Character`, `Expression`, `Word`, `Number`).

```
Read(input-stream, {object-type1, object-type2, ...})
```

> reads the `input-stream` AND RETURN A LIST OF EXPRESSIONS

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

* [Implementation of Read](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/FileFunctions.java#L910) 

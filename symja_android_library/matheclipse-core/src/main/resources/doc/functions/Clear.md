## Clear 

```
Clear(symbol1, symbol2,...)
```

> clears all values of the given symbols.

`Clear` does not remove attributes, options, and default values associated with the symbols. Use `ClearAll` to do so.
  
### Examples

``` 
>> a=2
2

>> Definition(a)
{a=2}
 
>> Clear(a)
>> a
a
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Clear](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/PatternMatching.java#L223) 

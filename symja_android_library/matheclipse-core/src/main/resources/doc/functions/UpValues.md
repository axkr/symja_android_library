## UpValues 

```
UpValues(symbol)
```
> prints the up-value rules associated with `symbol`.
  
### Examples

``` 
>> u /: v(x_u) := {x}

>> UpValues(u) 
{HoldPattern(v(x_u)):>{x}}
```

### Github

* [Implementation of UpValues](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/PatternMatching.java#L2713) 

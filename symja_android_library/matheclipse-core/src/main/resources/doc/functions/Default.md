## Default

```
Default(symbol)
```

> `Default` returns the default value associated with the `symbol` for a pattern default `_.` expression.
 
 ```ition)
```

> `Default` returns the default value associated with the `symbol` for a pattern default `_.` expression at position `pos`.

### Examples

```
>> Default(test) = 1 
1 

>> test(x_., y_.) = {x, y} 
{x,y} 
				
>> test(a) 
{a,1} 
				
>> test( ) 
{1,1}
```







### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Default](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/PatternMatching.java#L410) 

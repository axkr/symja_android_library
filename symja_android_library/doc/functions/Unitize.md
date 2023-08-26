## Unitize

```
Unitize(expr)
```

> maps a non-zero `expr` to `1`, and a zero `expr` to `0`. 
 
### Examples

```
>> Unitize((E + Pi)^2 - E^2 - Pi^2 - 2*E*Pi)
0
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Unitize](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/PiecewiseFunctions.java#L932) 

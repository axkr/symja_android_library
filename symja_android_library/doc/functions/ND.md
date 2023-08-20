## ND 
 
```
ND(function, x, value)
```

> returns a numerical approximation of the partial derivative of the `function` for the variable `x` and the given `value`.
 
```
ND(function, {x, n} , value)
```

> returns a numerical approximation of the partial derivative of order `n`.

### Examples

```
>> ND(BesselY(10.0,x), x, 1) 
1.20940*10^9

>> ND(Cos(x)^3, {x,2}, 1) 
1.82226
```

### Related terms 
[D](D.md), [DSolve](DSolve.md), [Integrate](Integrate.md), [Limit](Limit.md), [NIntegrate](NIntegrate.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of ND](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/ND.java#L65) 

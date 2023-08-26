## DifferenceDelta

```
DifferenceDelta(f(x), x)
```

> generates a forward difference `f(x+1) - f(x)`

```
DifferenceDelta(f(x), {x,n,h})
```

> generates a higher-order forward difference for integers `n`.

See:  
* [Wikipedia - Finite difference - Higher-order differences](https://en.wikipedia.org/wiki/Finite_difference#Higher-order_differences)

### Examples

```  
>> DifferenceDelta(b(a),a) 
-b(a)+b(1+a)

>> DifferenceDelta(b(a),{a,5,c})
-b(a)+5*b(a+c)-10*b(a+2*c)+10*b(a+3*c)-5*b(a+4*c)+b(a+5*c)
 
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of DifferenceDelta](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/DifferenceDelta.java#L50) 

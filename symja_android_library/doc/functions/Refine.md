## Refine

```
Refine(expression, assumptions)
```

> evaluate the `expression` for the given `assumptions`.
 

### Examples

```
>> Refine(Abs(n+Abs(m)), n>=0)
Abs(m)+n

>> Refine(-Infinity<x, x>0)
True 

>> Refine(Max(Infinity,x,y), x>0)
Max(Infinity,y)

>> Refine(Sin(k*Pi), Element(k, Integers))
0

>> Sin(k*Pi)
Sin(k*Pi)

>> Refine(D(Abs(x),x), Element(x, Reals)) 
x/Abs(x)

>> Refine(2/3*Round(x), Element(x,Integers))
2/3*x
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Refine](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/AssumptionFunctions.java#L306) 

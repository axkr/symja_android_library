## Element 

```
Element(symbol, domain)
```

> assume (or test) that the `symbol` is in the domain `domain`.

See:
* [Wikipedia - Domain of a function](https://en.wikipedia.org/wiki/Domain_of_a_function)

### Examples

```
>> Refine(Sin(k*Pi), Element(k, Integers))
0 

>> Refine(D(Abs(x),x), Element(x, Reals)) 
x/Abs(x) 
```







### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Element](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/AssumptionFunctions.java#L123) 

## Erf

```
Erf(z)
```

> returns the error function of `z`.
 
See
* [Wikipedia - Error function](https://en.wikipedia.org/wiki/Error_function)
* [Fungrim - Error functions](http://fungrim.org/topic/Error_functions/)



### Examples

`Erf(z)` is an odd function:

```
>> Erf(-x)
-Erf(x)
 
>> Erf(1.0)
0.8427007929497151
 
>> Erf(0)
0
 
>> {Erf(0, x), Erf(x, 0)}
{Erf(x),-Erf(x)}
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Erf](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/SpecialFunctions.java#L430) 

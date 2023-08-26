## RealValuedNumberQ

```
RealValuedNumberQ(expr)
```
> returns `True` if `expr` is an explicit real number with no imaginary component.

See
* [Wikipedia - Complex number](https://en.wikipedia.org/wiki/Complex_number)

### Examples

```
>> RealValuedNumberQ[10]
 = True
 
>> RealValuedNumberQ[4.0]
 = True
 
>> RealValuedNumberQ[1+I]
 = False
 
>> RealValuedNumberQ[0 * I]
 = True
 
>> RealValuedNumberQ[0.0 * I]
 = False
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of RealValuedNumberQ](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/PredicateQ.java#L1267) 

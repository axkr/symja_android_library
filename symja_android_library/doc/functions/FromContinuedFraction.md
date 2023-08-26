## FromContinuedFraction

```
FromContinuedFraction({n1, n2, ...})
```

> reconstructs a number from the list of its continued fraction terms `{n1, n2, ...}`.
  
See
* [Wikipedia - Continued fraction](https://en.wikipedia.org/wiki/Continued_fraction)
 
### Examples

```
>> FromContinuedFraction({2,3,4,5})
157/68

>> ContinuedFraction(157/68)
{2,3,4,5}

>> FromContinuedFraction({3, 7, 15, 1, 292, 1, 1, 1, 2, 1})
1146408/364913
 
>> FromContinuedFraction(Range(5))
225/157
        
```
 
### Related terms 
[ContinuedFraction](ContinuedFraction.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of FromContinuedFraction](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/NumberTheory.java#L2772) 

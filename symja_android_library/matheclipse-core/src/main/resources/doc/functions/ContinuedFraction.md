## ContinuedFraction

```
ContinuedFraction(number)
```
 
> the complete continued fraction representation for a rational or quadradic irrational `number`. 

```
ContinuedFraction(number, n)
```
 
> generate the first `n` terms in the continued fraction representation of `number`. 

See:  
* [Wikipedia - Continued fraction](https://en.wikipedia.org/wiki/Continued_fraction)
 
### Examples

```
>> FromContinuedFraction({2,3,4,5})
157/68

>> ContinuedFraction(157/68)
{2,3,4,5} 

>> ContinuedFraction(45/16)
{2,1,4,3}
```

For square roots of non-negative integer arguments `ContinuedFraction` determines the periodic part:

```
>> ContinuedFraction(Sqrt(13))
{3,{1,1,1,1,6}}

>> ContinuedFraction(Sqrt(919))
{30,{3,5,1,2,1,2,1,1,1,2,3,1,19,2,3,1,1,4,9,1,7,1,3,6,2,11,1,1,1,29,1,1,1,11,2,6,3,1,7,1,9,4,1,1,3,2,19,1,3,2,1,1,1,2,1,2,1,5,3,60}}
```

### Related terms 
[FromContinuedFraction](FromContinuedFraction.md), [QuadraticIrrationalQ](QuadraticIrrationalQ.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of ContinuedFraction](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/NumberTheory.java#L971) 

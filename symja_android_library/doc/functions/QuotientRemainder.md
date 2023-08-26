## QuotientRemainder

```
QuotientRemainder(m, n)
```

> computes a list of the quotient and remainder from division of `m` and `n`.

See 
* [Wikipedia - Quotient](https://en.wikipedia.org/wiki/Quotient)
* [Wikipedia - Remainder](https://en.wikipedia.org/wiki/Remainder)

### Examples

```
>> QuotientRemainder(23, 7)
{3,2}
```
 
Infinite expression QuotientRemainder(13, 0) encountered.

```
>> QuotientRemainder(13, 0)
QuotientRemainder(13, 0)

>> QuotientRemainder(-17, 7)
{-3,4}
 
>> QuotientRemainder(-17, -4)
{4,-1}
 
>> QuotientRemainder(19, -4)
{-5,-1}
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of QuotientRemainder](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/IntegerFunctions.java#L1407) 

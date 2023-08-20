## EffectiveInterest

```
EffectiveInterest(i, n)
```

> returns an effective interest rate object.
  

See:
* [Wikipedia - Annuity](https://en.wikipedia.org/wiki/Effective_interest_rate)
 
### Examples

```
>> TimeValue(Annuity(100, 12), EffectiveInterest(.01, 0.25), 12)
1268.515
```

### Related terms 
[Annuity](Annuity.md), [AnnuityDue](AnnuityDue.md), [TimeValue](TimeValue.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of EffectiveInterest](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/FinancialFunctions.java#L55) 

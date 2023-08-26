## Annuity

```
Annuity(p, t)
```

or 

```
Annuity(p, t, q)
```

> returns an annuity object.
  

See:
* [Wikipedia - Annuity](https://en.wikipedia.org/wiki/Annuity)
 
### Examples

```
>> TimeValue(Annuity(100, 12), .01, 0)
1125.508

>> TimeValue(Annuity(100, 12), EffectiveInterest(.01, 0.25), 12)
1268.515
```

### Related terms 
[AnnuityDue](AnnuityDue.md), [EffectiveInterest](EffectiveInterest.md), [TimeValue](TimeValue.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Annuity](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/FinancialFunctions.java#L29) 

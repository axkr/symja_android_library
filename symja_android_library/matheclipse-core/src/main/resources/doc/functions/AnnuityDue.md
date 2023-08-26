## AnnuityDue

```
AnnuityDue(p, t)
```

or 

```
AnnuityDue(p, t, q)
```

> returns an annuity due object.
  

See:
* [Wikipedia - Annuity-due](https://en.wikipedia.org/wiki/Annuity#Annuity-due)
 
### Examples

```
>> TimeValue(AnnuityDue(100, 12), 0.1, 0) 
749.5061
```

### Related terms 
[Annuity](Annuity.md), [EffectiveInterest](EffectiveInterest.md), [TimeValue](TimeValue.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of AnnuityDue](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/FinancialFunctions.java#L42) 

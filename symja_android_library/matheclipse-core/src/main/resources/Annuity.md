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
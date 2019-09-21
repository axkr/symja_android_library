## TimeValue

```
TimeValue(p, i, n)
```

> returns a time value calculation.
  

See:
* [Wikipedia - Time value of money](https://en.wikipedia.org/wiki/Time_value_of_money)
 
 
### Examples

```
>> TimeValue(Annuity(100, 12), .01, 0)
1125.508

>> TimeValue(Annuity(100, 12), EffectiveInterest(.01, 0.25), 12)
1268.515

>> TimeValue(AnnuityDue(100, 12), 0.1, 0) 
749.5061
```

### Related terms 
[Annuity](Annuity.md), [AnnuityDue](AnnuityDue.md), [EffectiveInterest](EffectiveInterest.md)

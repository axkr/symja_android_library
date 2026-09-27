## FromCoefficientRules

```
FromCoefficientRules(list-of-rules, list-of-variables)
```

> build the polynomial the rules `{exponent-vector -> coefficient, ...}` describe, which is the inverse of `CoefficientRules`.
 

See:  
* [Wikipedia - Coefficient](http://en.wikipedia.org/wiki/Coefficient)

### Examples

```
>> FromCoefficientRules({{2,0}->1, {1,1}->3, {0,0}->-5}, {x,y}) 
-5+x^2+3*x*y 

>> FromCoefficientRules({{2}->1, {0}->-1}, x) 
-1+x^2
```

It inverts `CoefficientRules`, for one polynomial or a list of them:

```
>> FromCoefficientRules(CoefficientRules(x^3-2*x*y+7, {x,y}), {x,y}) 
7+x^3-2*x*y 

>> FromCoefficientRules(CoefficientRules({x^2-1, y^3}, {x,y}), {x,y}) 
{-1+x^2,y^3}
```

A negative exponent gives a rational function:

```
>> FromCoefficientRules({{-1}->1}, {x}) 
1/x
```

### Related terms

[Coefficient](Coefficient.md), [CoefficientList](CoefficientList.md), [CoefficientRules](CoefficientRules.md), [Exponent](Exponent.md), [MonomialList](MonomialList.md)

### Implementation status

* &#x2705; - full supported

## Constant

```
Constant
```

> is an attribute that indicates that a symbol is a constant.
		
See:
* [Wikipedia - Constant (mathematics](https://en.wikipedia.org/wiki/Constant_(mathematics))

### Examples
```	  
Mathematical constants like `E` have attribute `Constant`:
```    
>> Attributes(E)    
{Constant}
``` 

Constant symbols cannot be used as variables in `Solve` and related functions.

E is a constant symbol and not a valid variable.  
```
>> Solve(x + E == 0, E)     
Solve(E+x==0,E)  
```

### Related terms 
[Attributes](Attributes.md), [ClearAttributes](ClearAttributes.md), [Flat](Flat.md), [HoldAll](HoldAll.md), [HoldFirst](HoldFirst.md), [HoldRest](HoldRest.md), [Listable](Listable.md), [NHoldAll](NHoldAll.md), [NHoldFirst](NHoldFirst.md), [NHoldRest](NHoldRest.md),  [Orderless](Orderless.md), [SetAttributes](SetAttributes.md)
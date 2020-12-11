## Listable

```
Listable
```

> is an attribute specifying that a function should be automatically applied to each element of a list.
		
### Examples

```
>> SetAttributes(f, Listable)    
>> f({1, 2, 3}, {4, 5, 6})    
{f(1,4),f(2,5),f(3,6)}     
 
>> f({1, 2, 3}, 4)    
{f(1,4),f(2,4),f(3,4)}    
 
>> {{1, 2}, {3, 4}} + {5, 6}    
{{6,7},{9,10}}   
```

### Related terms 
[Attributes](Attributes.md), [ClearAttributes](ClearAttributes.md), [Constant](Constant.md), [Flat](Flat.md), [HoldAll](HoldAll.md), [HoldFirst](HoldFirst.md), [HoldRest](HoldRest.md), [Listable](Listable.md), [NHoldAll](NHoldAll.md), [NHoldFirst](NHoldFirst.md), [NHoldRest](NHoldRest.md),  [Orderless](Orderless.md), [SetAttributes](SetAttributes.md)
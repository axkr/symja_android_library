## NHoldAll

```
NHoldAll
```

> is an attribute that protects all arguments of a function from numeric evaluation.
    
### Examples

```	
>> N(f(2, 3))    
f(2.0,3.0)   
 
>> SetAttributes(f, NHoldAll)    
>> N(f(2, 3))    
f(2,3)    
```


### Related terms 
[Attributes](Attributes.md), [ClearAttributes](ClearAttributes.md), [Constant](Constant.md), [Flat](Flat.md), [HoldAll](HoldAll.md), [HoldFirst](HoldFirst.md), [HoldRest](HoldRest.md), [Listable](Listable.md), [NHoldFirst](NHoldFirst.md), [NHoldRest](NHoldRest.md),  [Orderless](Orderless.md), [SetAttributes](SetAttributes.md)
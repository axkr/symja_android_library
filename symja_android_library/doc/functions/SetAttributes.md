## SetAttributes

```
SetAttributes(symbol, attrib)
```

> adds `attrib` to `symbol`'s attributes.
 
### Examples

```
>> SetAttributes(f, Flat) 
   
>> Attributes(f)    
{Flat}    
```

Multiple attributes can be set at the same time using lists:   
 
```
>> SetAttributes({f, g}, {Flat, Orderless})    
>> Attributes(g)    
{Flat, Orderless}    
```

### Related terms 
[Attributes](Attributes.md), [ClearAttributes](ClearAttributes.md),  [Constant](Constant.md), [Flat](Flat.md), [HoldAll](HoldAll.md),[HoldFirst](HoldFirst.md), [HoldRest](HoldRest.md), [Listable](Listable.md), [NHoldAll](NHoldAll.md), [NHoldFirst](NHoldFirst.md), [NHoldRest](NHoldRest.md),  [Orderless](Orderless.md)
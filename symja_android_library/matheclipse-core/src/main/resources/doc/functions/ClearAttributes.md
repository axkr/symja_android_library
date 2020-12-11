## ClearAttributes

```
ClearAttributes(symbol, attrib)
```

> removes `attrib` from `symbol`'s attributes.
 
### Examples

```
>> SetAttributes(f, Flat)

>> Attributes(f)    
{Flat}    
 
>> ClearAttributes(f, Flat)

>> Attributes(f)    
{}  
```
 
Attributes that are not even set are simply ignored:

```
>> ClearAttributes({f}, {Flat})    
>> Attributes(f)    
{}    
```

### Related terms 
[Attributes](Attributes.md),  [Constant](Constant.md), [Flat](Flat.md), [HoldAll](HoldAll.md),[HoldFirst](HoldFirst.md), [HoldRest](HoldRest.md), [Listable](Listable.md), [NHoldAll](NHoldAll.md), [NHoldFirst](NHoldFirst.md), [NHoldRest](NHoldRest.md), [Orderless](Orderless.md), [SetAttributes](SetAttributes.md)
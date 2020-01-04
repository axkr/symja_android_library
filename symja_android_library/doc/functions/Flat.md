## Flat

```
Flat
```

> is an attribute that specifies that nested occurrences of a function should be automatically flattened.    
 
See  
* [Wikipedia - Associative property](https://en.wikipedia.org/wiki/Associative_property)

### Examples

A symbol with the `Flat` attribute represents an associative mathematical operation:

```    
>> SetAttributes(f, Flat)    
>> f(a, f(b, c))    
f(a, b, c)    
```

`Flat` is taken into account in pattern matching:

```
>> f(a, b, c) /. f(a, b) -> d    
f(d, c)    
 
>> SetAttributes({u, v}, Flat)    
>> u(x_) := {x}    
>> u()    
u()    
 
>> u(a)    
{a}    
```

Iteration limit of 1000 exceeded.

```
>> u(a, b)    
```

Iteration limit of 1000 exceeded.

```
>> u(a, b, c)       
 
>> v(x_) := x    
>> v()    
v()    
 
>> v(a)    
a    
```

Iteration limit of 1000 exceeded.

```
>> v(a, b)   
v(a, b)   
```

Iteration limit of 1000 exceeded.

```
>> v(a, b, c)  
```

### Related terms 
[Constant](Constant.md),  [HoldAll](HoldAll.md), [HoldFirst](HoldFirst.md), [HoldRest](HoldRest.md), [Listable](Listable.md), [NHoldAll](NHoldAll.md), [NHoldFirst](NHoldFirst.md), [NHoldRest](NHoldRest.md),  [Orderless](Orderless.md)
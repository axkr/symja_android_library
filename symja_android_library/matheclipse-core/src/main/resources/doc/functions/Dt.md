## Dt

```
Dt(f, x)
```
> gives the total derivative of `f` with respect to `x`. 


```
Dt(f, x, y, ...)
```
> differentiates successively with respect to `x`, `y`, etc. 

```
Dt(f, {x,n})
```
> gives the multiple derivative of order `n`.  
   
See:
* [Wikipedia - Differentiation rules](https://en.wikipedia.org/wiki/Differentiation_rules)
* [Wikipedia - Derivative](https://en.wikipedia.org/wiki/Derivative)
* [Wikipedia - General Leibniz rule](https://en.wikipedia.org/wiki/General_Leibniz_rule)

### Examples
First-order derivative of a polynomial:

```
>> Dt(x^4, {x, 3}) 
24*x

>> Dt(x * y * z, x, Constants -> {y, z})
y*z
```

### Related terms 
[Derivative](Derivative.md), [D](D.md), [DSolve](DSolve.md), [Grad](Grad.md), [Integrate](Integrate.md), [Laplacian](Laplacian.md), [Limit](Limit.md), [ND](ND.md), [NIntegrate](NIntegrate.md) 

 
## Interpolation

```
Interpolation(data-list)
```

> return an `InterpolationFunction` for the `data-list`.

```
Interpolation(data-list, point)
```

> return the value for a given `point` interpolating the `data-list`.

 

### Examples

```
>> ipf=Interpolation(Table({x, Exp(4/(1+x^2))}, {x, 0, 3, 0.5})); ipf(2.5)
1.73624

>> Interpolation(Table({x, Exp(4/(1+x^2))}, {x, 0, 3, 0.5}), 2.5)
1.73624

>> Exp(4/(1 + 2.5^2)) 
1.73624
```
 
### Related terms 
[InterpolatingFunction](InterpolatingFunction.md) 


## DiscreteShift

```
DiscreteShift(f(var), {var, shift})
```

> `DiscreteShift` computes the shift `f(var+shift)`.
 
```
DiscreteShift(f(var), {var, shift, step})
```

> `DiscreteShift` computes the shift `f(var+shift*step)`.

### Examples

``` 
>> DiscreteShift(n^2, n)
(1+n)^2
```

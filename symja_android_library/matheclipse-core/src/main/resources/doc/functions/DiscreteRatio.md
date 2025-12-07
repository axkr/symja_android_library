## DiscreteRatio

```
DiscreteRatio(f(var), var)
```

> `DiscreteRatio` computes `f(var+1)/f(var)`.
 
```
DiscreteRatio(f(var), {var, n, step})
```

> `DiscreteRatio` computes the multiple discrete ratio with step `step`.

### Examples

``` 
>> DiscreteRatio(k!, k) 
1+k
``` 

## AsymptoticIntegrate

```
AsymptoticIntegrate(integral, {x, a, order})
```

> returns an asymptotic approximation for the `integral` to order `order`.

### Examples

```
>> AsymptoticIntegrate(Cos(t), {t, 0, x}, {x, 0, 4})
x-x^3/6+x^5/120

>> AsymptoticIntegrate(Cos(t), t->0)
t

>> AsymptoticIntegrate(Sin(x), x, x -> 0)
-1
```
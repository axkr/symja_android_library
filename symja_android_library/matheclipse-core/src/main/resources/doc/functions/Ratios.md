## Ratios

```
Ratios({x1, x2,...})
```

> computes the ratios `{x2/x1,x3/x2, x4/x2, x5/x4}`.
 
```
Ratios({x1, x2,...}, n)
```

> computes the `n`-th ratio iteration of `{x1, x2,...}`.
 
```
Ratios({x1, x2,...}, {n1, n2,...,nk})
```

> computes the `n_k`-th ratio iteration of `{x1, x2,...}`. at level `k`.

### Examples

``` 
>> Ratios({a,b,c,d,e,f,g,h},5)
{(b^5*d^10*f)/(a*c^10*e^5),(c^5*e^10*g)/(b*d^10*f^5),(d^5*f^10*h)/(c*e^10*g^5)}
```

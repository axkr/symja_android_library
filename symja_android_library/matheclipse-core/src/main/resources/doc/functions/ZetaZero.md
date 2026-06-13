## ZetaZero

```
ZetaZero(k)
```

> represents the `k`-th zero of the Riemann zeta function on the critical line.

```
ZetaZero(k, t)
```

> represents the `k`-th zero with imaginary part greater than `t`.


For positive `k`, `ZetaZero(k)` represents the zero on the critical line `1/2 + I*t` that has the
`k`-th smallest positive imaginary part. `N(ZetaZero(k))` gives a numerical approximation to the
specified zero and can be evaluated to arbitrary numerical precision.

See:
* [Wikipedia - Riemann zeta function](https://en.wikipedia.org/wiki/Riemann_zeta_function)
* [Wikipedia - Riemann hypothesis](https://en.wikipedia.org/wiki/Riemann_hypothesis)

### Examples

```
>> N(ZetaZero(1))
0.5+I*14.13473

>> N(ZetaZero(1), 20)
0.5+I*14.134725141734693790

>> N(ZetaZero(2, 20))
0.5+I*25.01086
``` 
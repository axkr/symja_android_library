## Convolve

```
Convolve(f, g, x, y)
```

> returns the convolution of `f` and `g` for the variable `x` transformed into the variable `y`.

`Convolve(f, g, x, y)` is defined as `Integrate(f(x)*g(y-x), {x, -Infinity, Infinity})`.

See:
* [Wikipedia - Convolution](https://en.wikipedia.org/wiki/Convolution)

### Examples

```
>> Convolve(UnitBox(x), UnitBox(x), x, y)
UnitTriangle(y)

>> Convolve(UnitStep(x), UnitStep(x), x, y)
y*UnitStep(y)

>> Convolve(DiracDelta(x), Sin(x), x, y)
Sin(y)

>> Convolve(E^(-x^2), E^(-x^2), x, y)
Sqrt(Pi/2)/E^(y^2/2)
```

### Related terms 
[ListConvolve](ListConvolve.md)


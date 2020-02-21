## PolyGamma

 
```
PolyGamma(value)
```

> return the digamma function of the `value`. The digamma function is defined as the logarithmic derivative of the gamma function.

```
PolyGamma(m, value)
```

> return the polygamma function of order `m` of the `value`. The polygamma function of order `m` is a meromorphic function on the complex numbers ℂ  defined as the (m + 1)-th derivative of the logarithm of the gamma function.
  
See
* [Wikipedia: Polygamma function](https://en.wikipedia.org/wiki/Polygamma_function)
* [Wikipedia: Digamma function](https://en.wikipedia.org/wiki/Digamma_function)

### Examples

```
>> PolyGamma(-1, 1)
0
```

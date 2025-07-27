## FindGeneratingFunction

```
FindGeneratingFunction({i1, i2, i3, ...}, var)
```

> searches for a unary generating function applied to the variable `var`, where the series coefficients equals `{i1, i2, i3, ...}`. 

**Note:** The algorithm of this function currently only calculates the Pade approximant to find a generating function.
  

See
* [Wikipedia - Generating function](https://en.wikipedia.org/wiki/Generating_function)
* [Wikipedia - Pade approximant](https://en.wikipedia.org/wiki/Pad%C3%A9_approximant)

### Examples

```
>> FindGeneratingFunction({1, 2, 3, 4}, x)
1/(1-2*x+x^2)
```

## PowerRange

```
PowerRange(base)
```

> Generates a list of powers from exponent `1` to `max`. Max is the largest power of '10` less equal `b ase`.

```
PowerRange(min, base)
```

> Generates a list of powers from `min` to `max`, with a factor of `10`.

```
PowerRange(min, base, factor)
```

> Generates a list of powers from `min` to `max`, with a factor of `factor`.

### Examples

```
>> PowerRange(1, x^10, Sqrt(x)) 
{1,Sqrt(x),x,x^(3/2),x^2,x^(5/2),x^3,x^(7/2),x^4,x^(9/2),x^5,x^(11/2),x^6,x^(13/2),x^7,x^(15/2),x^8,x^(17/2),x^9,x^(19/2),x^10}
```

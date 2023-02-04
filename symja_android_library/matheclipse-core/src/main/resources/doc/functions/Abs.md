## Abs

```
Abs(expr)
```

> returns the absolute value of the real or complex number `expr`.
  

See:
* [Wikipedia - Absolute value](http://en.wikipedia.org/wiki/Absolute_value)
 

### Examples

```
>> Abs(-3)
3
```

The derivative of `Abs` will not be evaluated because the system assumes `x` could be non-real:

```
>> D(Abs(x), x)
Abs'(x)
```

Use `RealAbs` to calculate the derivative:

```
>> D(RealAbs(x), x)
x/RealAbs(x)
```


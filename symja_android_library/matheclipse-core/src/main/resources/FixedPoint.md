## FixedPoint  

```
FixedPoint(f, expr)
```
 
> starting with `expr`, iteratively applies `f` until the result no longer changes.

```
FixedPoint(f, expr, n)
```

> performs at most `n` iterations.

See:
* [Wikipedia - Fixed-point iteration](https://en.wikipedia.org/wiki/Fixed-point_iteration)
* [Wikipedia - Newton's method](https://en.wikipedia.org/wiki/Newton%27s_method)

### Examples

```
>> FixedPoint(Cos, 1.0)
0.7390851332151607
 
>> FixedPoint(#+1 &, 1, 20)
21

>> FixedPoint(f, x, 0)
x
```

Non-negative integer expected.

```
>> FixedPoint(f, x, -1)
FixedPoint(f, x, -1)

>> FixedPoint(Cos, 1.0, Infinity)
0.739085
```

### Related terms 
[FixedPointList](FixedPointList.md) 
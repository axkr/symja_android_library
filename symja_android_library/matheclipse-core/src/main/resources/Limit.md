## Limit

```
Limit(expr, x->x0)
```

> gives the limit of `expr` as `x` approaches `x0`

```
Limit(expr, x->x0, Direction->-1)
```

> gives the limit as `x` decreases in value approaching `x0`  (`x` approaches `x0` "from the right" or "from above")

```
Limit(expr, x->x0, Direction->1)
```

> gives the limit as `x` increases in value approaching `x0` (`x` approaches `x0` "from the left" or "from below")

See:  
* [Wikipedia - Limit of a function](https://en.wikipedia.org/wiki/Limit_of_a_function)
* [Wikipedia - One-sided limit](https://en.wikipedia.org/wiki/One-sided_limit)

### Examples 

```
>> Limit(7+Sin(x)/x, x->Infinity)
7

>> Limit(x^(-2/3),x->0)
Indeterminate

>> Limit(x^(-2/3),x->0 , Direction->-1)
Infinity
```

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

The strings `"FromAbove"` and `"FromBelow"` can be used instead of `-1` and `1`; `Reals`, `Automatic`
or `"TwoSided"` select the default two-sided limit.

Functions with a jump discontinuity - `Floor`, `Ceiling`, `Round`, `IntegerPart`, `FractionalPart`,
`Sign`, `UnitStep`, `Mod`, `Quotient` - are evaluated on the side from which the limit point is
approached. A two-sided limit at such a jump is `Indeterminate`.

See:  
* [Wikipedia - Limit of a function](https://en.wikipedia.org/wiki/Limit_of_a_function)
* [Wikipedia - One-sided limit](https://en.wikipedia.org/wiki/One-sided_limit)
* [Wikipedia - L'Hôpital's rule](https://en.wikipedia.org/wiki/L%27H%C3%B4pital%27s_rule)

### Examples 

```
>> Limit(7+Sin(x)/x, x->Infinity)
7

>> Limit(x^(-2/3),x->0)
Indeterminate

>> Limit(x^(-2/3),x->0 , Direction->-1)
Infinity

>> Limit(Floor(x), x->2, Direction->"FromBelow")
1

>> Limit(Floor(x), x->2)
Indeterminate
```


### Related terms 
[D](D.md), [DSolve](DSolve.md), [Integrate](Integrate.md), [ND](ND.md), [NIntegrate](NIntegrate.md) 






### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of Limit](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/SeriesFunctions.java#L98) 

* [Rule definitions of Limit](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/rules/LimitRules.m) 

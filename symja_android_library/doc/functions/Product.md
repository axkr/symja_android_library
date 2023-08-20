## Product

```
Product(expr, {i, imin, imax})
```

> evaluates the discrete product of `expr` with `i` ranging from `imin` to `imax`.

```
Product(expr, {i, imin, imax, di})
```

> `i` ranges from `imin` to `imax` in steps of `di`.

```
Product(expr, {i, imin, imax}, {j, jmin, jmax}, ...)
```

> evaluates `expr` as a multiple sum, with `{i, ...}, {j, ...}, ...` being in outermost-to-innermost order.
		
See
* [Wikipedia - Multiplication - Capital_pi_notation](https://en.wikipedia.org/wiki/Multiplication#Capital_pi_notation)

### Examples

```
>> Product(k, {k, 1, 10})
3628800
 
>> 10!
3628800
 
>> Product(x^k, {k, 2, 20, 2})
x^110
 
>> Product(2 ^ i, {i, 1, n})
2^(1/2*n*(1+n))
```

Symbolic products involving the factorial are evaluated:

```
>> Product(k, {k, 3, n})
n! / 2
```

Evaluate the `n`th primorial:

```
>> primorial(0) = 1;
>> primorial(n_Integer) := Product(Prime(k), {k, 1, n});
>> primorial(12)
7420738134810
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Product](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/Product.java#L105) 

* [Rule definitions of Product](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/rule_sets/ProductRules.m) 

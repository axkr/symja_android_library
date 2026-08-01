## ToRadicals

```
ToRadicals(expr)
```

> replaces every `Root(f, k)` sub-expression in `expr` by its radical form, if one exists.

`Root(f, k)` only expands to radicals automatically for polynomials of degree 1 and 2.
`ToRadicals` also applies the Cardano and Ferrari formulas for degree 3 and 4, and falls back to
`Solve` for solvable polynomials of higher degree (e.g. binomials like `#^5 - 2`).

Polynomials which are not solvable in radicals are left unchanged - use `N` to evaluate those
roots numerically.

### Examples

```
>> ToRadicals(Root(#^2 - 2&, 1))
-Sqrt(2)

>> ToRadicals(Root(#^3 - 2&, 1))
2^(1/3)
```

The `k`-th root keeps the `Root` indexing convention - real roots first in ascending order, then
the complex roots by ascending real part and ascending imaginary part:

```
>> ToRadicals(Root(#^5 - 2&, 1))
2^(1/5)

>> ToRadicals(Root(#^5 - 2&, 3))
(-1)^(4/5)*2^(1/5)
```

`ToRadicals` maps over the whole expression and acts as the identity for everything else:

```
>> ToRadicals(Root((#^7-#^2-#+a)&, 1)+Root((#^6-#^2-#+a)&, 1))
Root(-#1-#1^2+#1^6+a&,1)+Root(-#1-#1^2+#1^7+a&,1)
```

A quintic which is not solvable in radicals stays unevaluated:

```
>> ToRadicals(Root(#^5 - # - 1&, 1))
Root(-1-#1+#1^5&,1)
```

### Related terms

[Root](Root.md), [Roots](Roots.md), [Solve](Solve.md)
 

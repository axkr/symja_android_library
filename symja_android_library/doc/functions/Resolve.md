## Resolve

```
Resolve(expr)
```

> eliminates the quantifiers `ForAll` and `Exists` from `expr`.

```
Resolve(expr, domain)
```

> eliminates the quantifiers with the variables ranging over the `domain`, `Reals` or `Complexes`.

The result is `True` or `False`, or, if `expr` has free parameters, a condition on these parameters which is equivalent to `expr`. Without a `domain`, the variables range over the `Reals` if `expr` contains an inequality, and over the `Complexes` otherwise.

The quantifiers can be nested. Quantifiers inside `And`, `Or`, `Not`, `Implies` and `Equivalent` are eliminated one by one, and the resulting conditions are combined without further simplification. `ForAll(x, cond, expr)` and `Exists(x, cond, expr)` restrict `x` to the values for which `cond` holds.

`Resolve` decides a quantifier with these strategies:

* a polynomial equation or inequality in the quantified variable is decided by the infimum and the supremum of the polynomial over the reals; if they depend on parameters, the condition on the parameters is returned
* a polynomial equation over the `Complexes` has a solution if the polynomial isn't constant
* an existence claim is proven by a witness, which is checked by substituting it into the condition
* a condition in one variable is solved by [Reduce](Reduce.md); an empty set of solutions refutes the existence claim

If none of the strategies applies, the expression stays unevaluated.

See:
* [Wikipedia - Quantifier elimination](https://en.wikipedia.org/wiki/Quantifier_elimination)

### Examples

```
>> Resolve(Exists(x, x^2 == -1), Reals)
False

>> Resolve(Exists(x, x^2 == -1), Complexes)
True

>> Resolve(ForAll(x, x^2 >= 0), Reals)
True

>> Resolve(ForAll(x, x^2 > 0), Reals)
False
```

Conditions on the parameters:

```
>> Resolve(Exists(x, x^2 == c), Reals)
c>=0

>> Resolve(ForAll(x, x^2 - 2*x + c > 0), Reals)
c>1

>> Resolve(Exists(x, x^2 + b*x + 1 == 0), Reals)
b^2>=4

>> Resolve(Exists(x, a*x == 1), Reals)
a!=0
```

Every real cubic polynomial has a real root:

```
>> Resolve(Exists(x, x^3 - x + c == 0), Reals)
True
```

Several variables, nested quantifiers, and a condition on the quantified variable:

```
>> Resolve(Exists({x, y}, x^2 + y^2 == 1 && x == y), Reals)
True

>> Resolve(ForAll(x, Exists(y, y > x)), Reals)
True

>> Resolve(ForAll(x, x > 0, x^2 > 0), Reals)
True
```

The inequality makes the variable real:

```
>> Resolve(Exists(x, x^2 < 0))
False
```

### Related terms
[Eliminate](Eliminate.md), [FindInstance](FindInstance.md), [Reduce](Reduce.md), [Solve](Solve.md)

### Implementation status

* &#x1F9EA; - experimental

### Github

* [Implementation of Resolve](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/Resolve.java)

## Implies

```
Implies(arg1, arg2)
```

> Logical implication. 

`Implies(A, B)` is equivalent to `!A || B`. `Implies(expr1, expr2)` evaluates each argument in turn, returning `True` as soon as the first argument evaluates to `False`. If the first argument evaluates to `True`, `Implies` returns the second argument.


See
* [Wikipedia - Logical consequence](https://en.wikipedia.org/wiki/Logical_consequence)

### Examples

```
>> Implies(False, a)
True
>> Implies(True, a)
a
```

If an expression does not evaluate to `True` or `False`, `Implies` returns a result in symbolic form:

```
>> Implies(a, Implies(b, Implies(True, c)))
Implies(a,Implies(b,c))
```
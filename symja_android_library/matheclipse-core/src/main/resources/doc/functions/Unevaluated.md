## Unevaluated

```
Unevaluated(expr)
```

> temporarily leaves `expr` in an unevaluated form when it appears as a function argument.
 
	
### Examples

`Unevaluated` is automatically removed when function arguments are evaluated:

```
>> Sqrt(Unevaluated(x))
Sqrt(x)
```

The `Length` value is `4` because we do not evaluate the `'`Plus` operator:

```
>> Length(Unevaluated(1+2+3+4))
4
```

`Unevaluated` has attribute `HoldAllComplete`:

```
>> Attributes(Unevaluated)
{HoldAllComplete,Protected}
```

The `Unevaluated` function call is kept in arguments of non-executed functions:

```
>> f(Unevaluated(x))
f(Unevaluated(x))
```

Functions that have the `Flat` property, `Unevaluated` propagates into function's arguments:

```
>> Attributes(f) = {Flat};

>> f(a, Unevaluated(f(b, c)))
f(a,Unevaluated(b),Unevaluated(c))
```

In `Sequence`s containing `Unevaluated` functions:

```
>> g(a, Sequence(Unevaluated(b),Unevaluated(c)))
```

However, when surrounding a `Sequence` by `Unevaluated`, no proliferation of `Unevaluated` function calls occurs:

```
>> g[Unevaluated[Sequence[a, b, c]]]
g(a,Unevaluated(b),Unevaluated(c))
```

### Related terms 
[Hold](Hold.md), [HoldComplete](HoldComplete.md), [HoldForm](HoldForm.md), [HoldPattern](HoldPattern.md), [ReleaseHold](ReleaseHold.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Unevaluated](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Programming.java#L3361) 

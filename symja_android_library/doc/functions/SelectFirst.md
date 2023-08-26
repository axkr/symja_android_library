## SelectFirst

```
SelectFirst({e1, e2, ...}, f)
```

> returns the first of the elements `ei` for which `f(ei)` returns `True`.

```
SelectFirst({e1, e2, ...}, f, default)
```

> if `f(ei)` returns `False`, the `default` value will be returned


### Examples

Find first number greater than zero:

```
>> SelectFirst({-3, 0, 1, 3, a}, #>0 &)
1

>> SelectFirst({-3, 0, 1, 3, a}, #>3 &) 
Missing(NotFound)
```

[OEIS - A134860](https://oeis.org/A134860):

```
>> With({r = Map(Fibonacci, Range(2, 14))}, Position(#, {1, 0, 1})[[All, 1]] &@ Table(If(Length@ # < 3, {}, Take(#, -3)) &@ IntegerDigits@ Total@ Map(FromDigits@ PadRight({1}, Flatten@ #) &@ Reverse@ Position(r, #) &, Abs@ Differences@ NestWhileList(Function(k, k - SelectFirst(Reverse@ r, # < k &)), n + 1, # > 1 &)), {n, 373}))

{4,12,17,25,33,38,46,51,59,67,72,80,88,93,101,106,114,122,127,135,140,148,156,
161,169,177,182,190,195,203,211,216,224,232,237,245,250,258,266,271,279,284,292,
300,






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of SelectFirst](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ListFunctions.java#L6629) 

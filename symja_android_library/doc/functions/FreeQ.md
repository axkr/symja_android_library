## FreeQ

```
FreeQ(expr, x)
```

> returns `True` if `expr` does not contain the expression `x`.

```
FreeQ(expr, x, levelspec)
```

> tests only the parts of `expr` at the levels `levelspec`. The option `Heads -> False` leaves the heads out.

Like in WMA, `FreeQ` sees the head `Complex` or `Rational` of a complex or rational number, but not its parts: `FreeQ(1+2*I, Complex)` and `FreeQ(1/2, Rational)` are `False`, `FreeQ(1/2, 2)` is `True`.

### Examples

```
>> FreeQ(y, x)
True

>> FreeQ(a+b+c, a+b)
False

>> FreeQ({1, 2, a^(a+b)}, Plus)
False

>> FreeQ(a+b, x_+y_+z_)
True

>> FreeQ(a+b+c, x_+y_+z_)
False

>> FreeQ(x_+y_+z_)(a+b)
True

>> FreeQ(1+2*I, Complex)
False

>> FreeQ(f(g(x)), x, {1})
True
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of FreeQ](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/PredicateQ.java#L548) 

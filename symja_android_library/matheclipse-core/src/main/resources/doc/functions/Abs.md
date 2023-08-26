## Abs

```
Abs(expr)
```

> returns the absolute value of the real or complex number `expr`.
  

See:
* [Wikipedia - Absolute value](http://en.wikipedia.org/wiki/Absolute_value)
 

### Examples

```
>> Abs(-3)
3
```

The derivative of `Abs` will not be evaluated because the system assumes `x` could be non-real:

```
>> D(Abs(x), x)
Abs'(x)
```

Use `RealAbs` to calculate the derivative:

```
>> D(RealAbs(x), x)
x/RealAbs(x)
```







### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Abs](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Arithmetic.java#L243) 

* [Rule definitions of Abs](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/rules/AbsRules.m) 

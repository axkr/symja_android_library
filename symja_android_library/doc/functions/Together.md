## Together

```
Together(expr)
```

> writes sums of fractions in `expr` together.

### Examples

```
>> Together(a/b+x/y)
(a*y+b*x)*b^(-1)*y^(-1)

>> Together(a / c + b / c)
(a+b)/c
```

`Together` operates on lists:

```
>> Together({x / (y+1) + x / (y+1)^2})
{x (2 + y) / (1 + y) ^ 2}
```

But it does not touch other functions:

```
>> Together(f(a / c + b / c))
f(a/c+b/c)

>> f(x)/x+f(x)/x^2//Together
f(x)/x^2+f(x)/x
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Together](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Algebra.java#L4438) 

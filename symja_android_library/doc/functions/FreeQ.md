## FreeQ

```
FreeQ(`expr`, `x`)
```

> returns 'True' if `expr` does not contain the expression `x`.

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
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of FreeQ](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/PredicateQ.java#L530) 

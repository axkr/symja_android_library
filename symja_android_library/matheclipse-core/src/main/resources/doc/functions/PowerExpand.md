## PowerExpand

```
PowerExpand(expr)
```

> expands out powers of the form `(x^y)^z` and `(x*y)^z` in `expr`.

### Examples

```
>> PowerExpand((a ^ b) ^ c)
a^(b*c)

>> PowerExpand((a * b) ^ c)
a^c*b^c

>> PowerExpand(Log(x/y))
Log(x)-Log(y)
```

`PowerExpand` is not correct without certain assumptions:

```
>> PowerExpand((x ^ 2) ^ (1/2))
x
```

### Github

* [Implementation of PowerExpand](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Algebra.java#L3813) 

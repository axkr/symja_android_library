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

>> PowerExpand(Log(-13/350))
I*Pi-Log(2)-2*Log(5)-Log(7)+Log(13)
        
```

`PowerExpand` is not correct without certain assumptions:

```
>> PowerExpand((x ^ 2) ^ (1/2))
x
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of PowerExpand](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Algebra.java#L3946) 

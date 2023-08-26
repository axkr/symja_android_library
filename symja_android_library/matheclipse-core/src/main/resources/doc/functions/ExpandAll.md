## ExpandAll

```
ExpandAll(expr)
```

> expands out all positive integer powers and products of sums in `expr`. 

### Examples

```
>> ExpandAll((a + b) ^ 2 / (c + d)^2)
(a^2+2*a*b+b^2)/(c^2+2*c*d+d^2)
```

`ExpandAll` descends into sub expressions

```
>> ExpandAll((a + Sin(x*(1 + y)))^2)
a^2+Sin(x+x*y)^2+2*a*Sin(x+x*y) 
```

`ExpandAll` also expands heads

```
>> ExpandAll(((1 + x)(1 + y))[x])
(1+x+y+x*y)[x]
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of ExpandAll](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Algebra.java#L2016) 

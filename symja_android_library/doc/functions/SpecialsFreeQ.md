## SpecialsFreeQ

```
SpecialsFreeQ(expr)
```

> returns `True` if `expr` does not contain the symbols `DirectedInfinity` or `Indeterminate`.

### Examples

```
>> SpecialsFreeQ(-Infinity)
False

>> SpecialsFreeQ(1+x^2)
True
```

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of SpecialsFreeQ](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/PredicateQ.java#L1493) 

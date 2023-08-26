## Reduce

```
Reduce(logic-expression, var)
```

> returns the reduced `logic-expression` for the variable `var`. Reduce works only for the `Reals` domain.

### Examples

```
>> Reduce(x^6-1==0 && x>0, x)
x==1
```

### Related terms 
[Solve](Solve.md), [NSolve](NSolve.md), [Roots](Roots.md), [NRoots](NRoots.md)  






### Implementation status

* &#x1F9EA; - experimental

### Github

* [Implementation of Reduce](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/Reduce.java#L20) 

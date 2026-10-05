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

Two real variables, if the second one occurs with degree 1 in every factor which contains the first one:

```
>> Reduce(x/y<=0, {x,y}, Reals)
(x<0&&y>0)||(x==0&&(y<0||y>0))||(x>0&&y<0)

>> Reduce(x*y>1 && y<x, {x,y}, Reals)
(x<=-1&&y<x)||(x>-1&&x<0&&y<1/x)||(x>1&&y>1/x&&y<x)
```

### Related terms 
[Solve](Solve.md), [NSolve](NSolve.md), [Roots](Roots.md), [NRoots](NRoots.md)  






### Implementation status

* &#x1F9EA; - experimental

### Github

* [Implementation of Reduce](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/Reduce.java#L24) 

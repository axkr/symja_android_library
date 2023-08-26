## NSolve 

```
NSolve(equations, vars)
```

> attempts to solve `equations` for the variables `vars`.

```
NSolve(equations, vars, domain)
```

> attempts to solve `equations` for the variables `vars` in the given `domain`.

**Note:** `NSolve` calls [Solve](Solve.md) in numeric mode.

### Examples

```
>> NSolve({Sin(x)-11==y, x+y==-9}, {y,x})
{x->1.1060601577062719,y->-10.106060157706272}
```

### Related terms 
[Solve](Solve.md), [Reduce](Reduce.md), [Roots](Roots.md) 






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of NSolve](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/NSolve.java#L13) 

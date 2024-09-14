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

It's important to use the `==` operator to define the equations. If you have unintentionally assigned a value to the variables `x, y` with the `=` operator you have to call `Clear(x,y)` to clear the definitions for these variables.

```
>> NSolve({Sin(x)-11==y, x+y==-9}, {y,x})
{x->1.1060601577062719,y->-10.106060157706272}
```

### Related terms 
[Solve](Solve.md), [Reduce](Reduce.md), [Roots](Roots.md) 






### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of NSolve](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/NSolve.java#L14) 

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
[Solve](Solve.md)
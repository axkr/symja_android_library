## FindMaximum

```
FindMaximum(f, {x, xstart})
```

> searches for a local numerical maximum of `f` for the variable `x` and the start value `xstart`. 

```
FindMaximum(f, {x, xstart}, Method->method_name)
```

> searches for a local numerical maximum of `f` for the variable `x` and the start value `xstart`, with one of the following method names:

```
FindMaximum(f, {{x, xstart},{y, ystart},...})
```

> searches for a local numerical maximum of the multivariate function `f` for the variables `x, y,...` and the corresponding start values `xstart, ystart,...`. 

See
* [Wikipedia - Mathematical optimization](https://en.wikipedia.org/wiki/Mathematical_optimization)

#### Powell

Implements the Powell optimizer. 

This is the default method, if no `method_name` is given.

#### ConjugateGradient

Implements the ConjugateGradient optimizer.  
This is a derivative based method and the functions must be symbolically differentiatable.

### Examples

```
>> FindMaximum(Sin(x), {x, 0.5}) 
{1.0,{x->1.5708}}
```

### Related terms 
[FindMinimum](FindMinimum.md), [FindRoot](FindRoot.md), [NRoots](NRoots.md), [Solve](Solve.md)
 






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of FindMaximum](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/FindMaximum.java#L73) 

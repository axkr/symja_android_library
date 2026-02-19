## DesignMatrix
```
DesignMatrix(m, f, x)
```
> returns the design matrix.

### Examples
```
>> DesignMatrix({{2, 1}, {3, 4}, {5, 3}, {7, 6}}, x, x)
{{1,2},{1,3},{1,5},{1,7}}
 
>> DesignMatrix({{2, 1}, {3, 4}, {5, 3}, {7, 6}}, f(x), x)
{{1,f(2)},{1,f(3)},{1,f(5)},{1,f(7)}}


>> data = {{0, 1}, {1, 3}, {2, 4}, {3, 7}}; basis = {1, x}; m = DesignMatrix(data, basis, x); v = data[[All, 2]]; LinearModelFit({m, v}) 
FittedModel[0.9*#1+1.9*#2]
```

### Related terms 
[FittedModel](FittedModel.md), [LinearModelFit](LinearModelFit.md)

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of DesignMatrix](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/LinearAlgebra.java#L1387) 

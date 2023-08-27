## LinearModelFit

```
LinearModelFit({{x1,y1},{x2,y2},...}, expr, symbol)
```

> Create a linear regression model from a matrix of observed value pairs `{x_i, y_i}`.
 
```
LinearModelFit({y1,y2,y3...}, expr, symbol)
```
> Create  a linear regression model from a vector of observed value pairs `{{1.0, y1},{2.0, y2},{3.0, y3}...}`.
   
See:  
* [Wikipedia - Linear regression](https://en.wikipedia.org/wiki/Linear_regression) 
 
### Examples

```
>> LinearModelFit({ { 1, 3 }, { 2, 5 }, { 3, 7 }, { 4, 14 }, { 5, 11 } },x,x) // Normal
0.5+2.5*x
 
```

### Related terms 
[Fit](Fit.md), [FindFit](FindFit.md),[FittedModel](FittedModel.md) 






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of LinearModelFit](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/CurveFitterFunctions.java#L324) 

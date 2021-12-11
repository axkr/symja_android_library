## LinearModelFit

```
LinearModelFit(list-of-data-points, expr, symbol)
```
 
> In statistics, linear regression is a linear approach to modeling the relationship between a scalar response (or dependent variable) and one or more explanatory variables (or independent variables).
 
   
See:  
* [Wikipedia - Linear regression](https://en.wikipedia.org/wiki/Linear_regression) 
 
### Examples

```
>> LinearModelFit({ { 1, 3 }, { 2, 5 }, { 3, 7 }, { 4, 14 }, { 5, 11 } },x,x) // Normal
0.5+2.5*x
 
```

### Related terms 
[Fit](Fit.md), [FindFit](FindFit.md),[FittedModel](FittedModel.md) 

### Github

* [Implementation of LinearModelFit](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/CurveFitterFunctions.java#L346) 

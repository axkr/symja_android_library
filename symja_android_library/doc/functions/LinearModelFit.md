## LinearModelFit

```
LinearModelFit({{x11,x12,y1},{x21,x22,y2},...}, {Func1, func2,...}, {var1, var2,...})
```

> Create a linear regression model from a matrix of observed value pairs `{x_ij,..., y_i}`.
 
```
LinearModelFit({y1,y2,y3,...}, expr, symbol)
```

> Create  a linear regression model from a vector of observed value pairs `{{1.0, y1},{2.0, y2},{3.0, y3},...}`.
   
```
LinearModelFit({design-matrix, response-vector})
```

> Create  a linear regression model from `design-matrix` and `response-vector`.
   

See:  
* [Wikipedia - Linear regression](https://en.wikipedia.org/wiki/Linear_regression) 
 
### Examples

```
>> nlm=LinearModelFit({{1, 1, 6.5}, {2, 1, 8.4}, {1, 2, 9.6}, {2, 2, 12.1}, {3, 2, 14.0}},{1, a, b, a*b},{a, b})
FittedModel[1.7+1.6*a+2.9*b+0.3*a*b]

>>nlm // Normal
1.7+1.6*a+2.9*b+0.3*a*b
```

The following properties are supported for `FittedModel`:

```     
>> nlm("BestFit")
1.7+1.6*a+2.9*b+0.3*a*b");

>> nlm("BestFitParameters") 
{1.7,1.6,2.9,0.3}");

>> nlm("EstimatedVariance")
0.06

>> nlm("FitResiduals")
{5.32907*10^-15,1.77636*10^-15,-0.1,0.2,-0.1} 

>> nlm("ParameterErrors"
{1.15758,0.714143,0.663325,0.387298}

>> nlm("RSquared")
0.99989
        
>> LinearModelFit({ { 1, 3 }, { 2, 5 }, { 3, 7 }, { 4, 14 }, { 5, 11 } },x,x) // Normal
FittedModel[0.5+2.5*x]
```

### Related terms 
[DesignMatrix](DesignMatrix.md), [Fit](Fit.md), [FindFit](FindFit.md), [FittedModel](FittedModel.md) 


### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of LinearModelFit](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/CurveFitterFunctions.java#L356) 

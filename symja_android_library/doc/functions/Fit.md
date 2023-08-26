## Fit  

```
Fit(list-of-data-points, degree, variable)
```
 
> solve a least squares problem using the Levenberg-Marquardt algorithm.
   
See:  
* [Wikipedia - Levenberg–Marquardt algorithm](https://en.wikipedia.org/wiki/Levenberg%E2%80%93Marquardt_algorithm) 
 
### Examples

```
>> Fit({{1,1},{2,4},{3,9},{4,16}},2,x)
x^2.0
```

### Related terms 
[FindFit](FindFit.md), [FittedModel](FittedModel.md), [LinearModelFit](LinearModelFit.md) 






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Fit](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/CurveFitterFunctions.java#L293) 

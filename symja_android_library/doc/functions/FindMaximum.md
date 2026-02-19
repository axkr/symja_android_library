## FindMaximum

```
FindMaximum(f, {x, xstart})
```

> searches for a local numerical maximum of `f` for the variable `x` and the start value `xstart`. 

```
FindMaximum(f, {x, xstart}, Method->methodName)
```

> searches for a local numerical maximum of `f` for the variable `x` and the start value `xstart`, with one of the following method names:

```
FindMaximum(f, {{x, xstart},{y, ystart},...})
```

> searches for a local numerical maximum of the multivariate function `f` for the variables `x, y,...` and the corresponding start values `xstart, ystart,...`. 

See
* [Wikipedia - Mathematical optimization](https://en.wikipedia.org/wiki/Mathematical_optimization)
* [Wikipedia - Rosenbrock function](https://en.wikipedia.org/wiki/Rosenbrock_function)

#### "Powell"

Implements the [Powell](https://github.com/Hipparchus-Math/hipparchus/blob/master/hipparchus-optim/src/main/java/org/hipparchus/optim/nonlinear/scalar/noderiv/PowellOptimizer.java) optimizer. 

This is the default method, if no `Method` is set.

#### "ConjugateGradient"

Implements the [Non-linear conjugate gradient](https://github.com/Hipparchus-Math/hipparchus/blob/main/hipparchus-optim/src/main/java/org/hipparchus/optim/nonlinear/scalar/gradient/NonLinearConjugateGradientOptimizer.java) optimizer.  
This is a derivative based method and the functions must be symbolically differentiable.

#### "SequentialQuadratic"

Implements the [Sequential Quadratic Programming](https://github.com/Hipparchus-Math/hipparchus/blob/main/hipparchus-optim/src/main/java/org/hipparchus/optim/nonlinear/vector/constrained/SQPOptimizerS.java) optimizer.  

This is a derivative, multivariate based method and the functions must be symbolically differentiable.

#### "BOBYQA"

Implements [Powell's BOBYQA](https://github.com/Hipparchus-Math/hipparchus/blob/master/hipparchus-optim/src/main/java/org/hipparchus/optim/nonlinear/scalar/noderiv/BOBYQAOptimizer.java) optimizer (Bound Optimization BY Quadratic Approximation). 

The "BOBYQA" method falls back to "CMAES" if the objective function has dimension 1.

#### "CMAES"

Implements the [Covariance Matrix Adaptation Evolution Strategy (CMA-ES)](https://github.com/Hipparchus-Math/hipparchus/blob/master/hipparchus-optim/src/main/java/org/hipparchus/optim/nonlinear/scalar/noderiv/BOBYQAOptimizer.java) optimizer. 
 
### Examples

```
>> FindMaximum(Sin(x), {x, 0.5}) 
{1.0,{x->1.5708}}

>> FindMaximum({(1-x)^2+100*(y-x^2)^2, x >= -2.0 && 2.0 >= x && y >= -0.5 && 1.5 >= y}, {{x, -1.2}, {y,1.0}}, Method->"BOBYQA") 
{2034.0,{x->-2.0,y->-0.5}}
```

### Related terms 
[FindMinimum](FindMinimum.md), [FindRoot](FindRoot.md), [NRoots](NRoots.md), [Solve](Solve.md)
  

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of FindMaximum](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/FindMaximum.java#L113) 

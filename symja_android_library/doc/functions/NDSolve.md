## NDSolve

```
NDSolve({equation-list}, functions, t)
```
> attempts to solve the linear differential `equation-list` for the `functions` and the time-dependent-variable `t`. Returns an `InterpolatingFunction` function object.

See:  
* [Wikipedia - Ordinary differential equation](https://en.wikipedia.org/wiki/Ordinary_differential_equation)

### Examples

Example taken from [Tutorial — Differential Equations](https://socialinnovationsimulation.com/2013/07/19/tutorial-differential-equations-2/)

```
>> model=NDSolve({x'(t) == 10*(y(t) - x(t)), y'(t) == x(t)*(28 - z(t)) - y(t), z'(t) == x(t)*y(t) - 8/3*z(t), x(0)== 0, y(0) == 1, z(0) == 0}, {x, y, z}, {t, 0, 20})

{{x->InterpolatingFunction({{0.0,20.0}},<>),y->InterpolatingFunction({{0.0,20.0}},<>),z->InterpolatingFunction({{0.0,20.0}},<>)}}
```

An `InterpolatingFunction` prints as the range it is defined on. Bind one to a symbol to sample the solution - only an identifier can head a function application, so `(y /. Part(m, 1))(0.5)` would be read as a multiplication instead:

```
>> m = NDSolve({y'(x) == y(x), y(0) == 1}, y, {x, 0, 1}); f = y /. Part(m, 1); f(0.5)

1.64872
```

Equations of higher order are reduced automatically:

```
>> s = NDSolve({y''(x) + y(x) == 0, y(0) == 1, y'(0) == 0}, y, {x, 0, 4}); g = y /. Part(s, 1); g(N(Pi))

-1.0
```

The initial conditions may be given at any point of the range, not only at its start. Here they are given at `t == 1` and the equation is integrated in both directions to cover `{t, 0, 2}`:

```
>> h = NDSolve({w'(t) == w(t), w(1) == 1}, w, {t, 0, 2}); fw = w /. Part(h, 1); {fw(0.0), fw(2.0)}

{0.367879,2.71828}
```

Plot the interpolating function and the sine function.

```
>> Plot({Evaluate(z(t) /.model)}, {t, 0, 20})
```

A solution which blows up before the end of the requested range comes back defined only on the range which could be integrated, rather than not at all. `y'(t) == y(t)^2, y(0) == 1` is `1/(1-t)`, whose pole at `t == 1` stops the stepping well short of the requested `t == 2`:

```
>> b = NDSolve({y'(t) == y(t)^2, y(0) == 1}, y, {t, 0, 2}); fb = y /. Part(b, 1); fb(0.5)

2.0
```

This is what a shooting method needs: it guesses initial values whose trajectory diverges all the time, and failing the whole call on such a guess would break the search which made it. An `NDSolve::ndsz` message reports where the stepping gave up, and sampling the solution beyond that point extrapolates and says so.

### Options

`AccuracyGoal` sets the absolute and `PrecisionGoal` the relative local error, each as a number of digits. `Automatic` asks for twelve digits of both, which is tighter than the default Mathematica uses at machine precision, because the accuracy of a located event is bounded by it.

```
>> f=NDSolveValue({y'(x)==y(x),y(0)==1},y,{x,0,1},AccuracyGoal->6,PrecisionGoal->6);f(1.0)

2.71828
```

`Method` selects the time integration method: `"ExplicitRungeKutta"` (the default), `"Adams"`, `"Extrapolation"` or `"FixedStep"`. The stiff solvers - `"BDF"`, `"StiffnessSwitching"`, `"IDA"`, `"ImplicitRungeKutta"` and the `"LinearlyImplicit..."` submethods - are not available.

```
>> f=NDSolveValue({y'(x)==y(x),y(0)==1},y,{x,0,1},Method->"Adams");f(1.0)

2.71828
```

### EventLocator

`Method->{"EventLocator", ...}` stops or interrupts the integration where an expression crosses zero. Its sub-options are `"Event"` (an expression, or a list of them), `"EventAction"` (default `Throw(Null,"StopIntegration")`), `"Direction"` (`1`, `-1` or `All`), `"EventCondition"`, `"EventLocationMethod"` (`"Brent"` or `"LinearInterpolation"`) and a nested `Method`.

The action has to be given with `:>` rather than `->`, so that it is held until the event is reached and `t` and the dependent functions in it are the numbers of that moment:

```
>> NDSolve({y'(t)==-y(t),y(0)==1},y,{t,0,10},Method->{"EventLocator","Event"->y(t)-0.5,"EventAction":>Throw(stop=t,"StopIntegration")});stop

0.693147
```

An action which does not throw lets the integration continue, so events can be counted or collected. `"Direction"->1` restricts this to the crossings where the event expression increases:

```
>> n=0;NDSolve({y''(t)+y(t)==0,y(0)==1,y'(0)==0},y,{t,0,10},Method->{"EventLocator","Event"->y(t),"Direction"->1,"EventAction":>(n=n+1)});n

1
```

Events are only looked for where the event expression changes sign between two steps, so more than one crossing within a single step can be missed.

### Related terms
[DSolve](DSolve.md), [InterpolatingFunction](InterpolatingFunction.md), [NRoots](NRoots.md), [Solve](Solve.md)






### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of NDSolve](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/NDSolve.java#L28) 

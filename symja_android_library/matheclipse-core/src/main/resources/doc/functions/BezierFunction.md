## BezierFunction

```
BezierFunction(list-of-control-points) 
```

> Bezier curve constructed by `list-of-control-points` 
 
See
* [Wikipedia - Bezier_curve](https://en.wikipedia.org/wiki/B%C3%A9zier_curve)


### Examples

```
>> f = BezierFunction({{0, 0}, {0, 0.8}, {1, 1}}); FindRoot(0.5==Indexed(f(x), 2), {x, 0.2, 0.5}, Method->"Bisection")
{x->0.361508}
```

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of BezierFunction](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/BezierFunction.java#L15) 

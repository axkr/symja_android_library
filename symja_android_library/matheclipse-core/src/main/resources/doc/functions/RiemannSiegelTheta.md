## RiemannSiegelTheta

```
RiemannSiegelTheta(t)
```

> gives the Riemann-Siegel function `theta(t)`.

For real `t`, `RiemannSiegelTheta(t) = Im(LogGamma(1/4 + I*t/2)) - t/2*Log(Pi)`. It arises in the
study of the Riemann zeta function on the critical line and is closely related to the number of
zeros of `Zeta(1/2 + I*u)`. `RiemannSiegelTheta` can be evaluated to arbitrary numerical precision
and automatically threads over lists.

See:
* [Wikipedia - Riemann-Siegel theta function](https://en.wikipedia.org/wiki/Riemann%E2%80%93Siegel_theta_function)

### Examples

```
>> RiemannSiegelTheta(0)
0

>> N(RiemannSiegelTheta(10))
-3.06707

>> N(RiemannSiegelTheta(10), 20)
-3.0670692459732012751
```

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of RiemannSiegelTheta](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/RiemannSiegelTheta.java)

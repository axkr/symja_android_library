## Laplacian

```
Laplacian(function, {x1, x2, ... , xN})
```

> returns the Laplace operator

```
Laplacian(function, {x1, x2, ... , xN}, metric-string)
```

> returns the Laplace operator for the given `metric-string`. Possible metrics are `"Cartesian", "Cylindrical", "Polar", "Spherical"`.
 

See:  
* [Wikipedia - Laplace operator](https://en.wikipedia.org/wiki/Laplace_operator)

### Examples

```
>> Laplacian(Sin(x^2 + y^2), {x, y}) // Simplify 
4*(Cos(x^2+y^2)-x^2*Sin(x^2+y^2)-y^2*Sin(x^2+y^2)) 

>> Laplacian(Sin(x^2 + y^2), {x, y}, "Cartesian") 
4*Cos(x^2+y^2)-4*x^2*Sin(x^2+y^2)-4*y^2*Sin(x^2+y^2) 

>> Laplacian(Sin(r^2), {r, t}, "Polar") 
4*Cos(r^2)-4*r^2*Sin(r^2)
```


### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of Laplacian](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/VectorAnalysisFunctions.java#L879) 

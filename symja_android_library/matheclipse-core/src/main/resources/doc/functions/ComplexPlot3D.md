## ComplexPlot3D
 
```
ComplexPlot3D(expr, {z, min, max )
```

> create a 3D plot of `expr` for the complex variable `z` in the range `{ Re(min),Re(max) }` to `{ Im(min),Im(max) }`

See  
* [Wikipedia - Complex number](https://en.wikipedia.org/wiki/Complex_number) 
* [Wikipedia - Complex plane](https://en.wikipedia.org/wiki/Complex_plane) 
* [Wikipedia - Domain coloring](https://en.wikipedia.org/wiki/Domain_coloring)

### Examples

```
>> ComplexPlot3D(Gamma(z), {z, -4.9-4.9*I,4.9+4.9*I}, PlotRange->{0,8.0})

```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of ComplexPlot3D](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ManipulateFunction.java#L1876) 

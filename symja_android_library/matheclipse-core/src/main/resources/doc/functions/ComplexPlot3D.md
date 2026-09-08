## ComplexPlot3D
 
```
ComplexPlot3D(expr, {z, min, max})
```

> create a 3D plot of `expr` for the complex variable `z` in the range `{ Re(min),Re(max) }` to `{ Im(min),Im(max) }`

The height of the surface is `Abs(expr)` and its colour is the domain colouring
[ComplexPlot](ComplexPlot.md) uses, so `ColorFunction` takes the same settings here: the shading
scheme names, the `{cfunc, sfunc}` pair, and a colour function of your own given the eight
arguments `Re(z)`, `Im(z)`, `Abs(z)`, `Arg(z)`, `Re(f)`, `Im(f)`, `Abs(f)`, `Arg(f)`.

See  
* [Wikipedia - Complex number](https://en.wikipedia.org/wiki/Complex_number) 
* [Wikipedia - Complex plane](https://en.wikipedia.org/wiki/Complex_plane) 
* [Wikipedia - Domain coloring](https://en.wikipedia.org/wiki/Domain_coloring)

### Examples

```
>> ComplexPlot3D(Gamma(z), {z, -4.9-4.9*I,4.9+4.9*I}, PlotRange->{0,8.0})

```

```
>> ComplexPlot3D(z^2, {z, -1-I, 1+I}, ColorFunction -> "CyclicLogAbsArg")

```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of ComplexPlot3D](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/graphics3d/ComplexPlot3D.java#L21) 

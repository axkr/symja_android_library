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
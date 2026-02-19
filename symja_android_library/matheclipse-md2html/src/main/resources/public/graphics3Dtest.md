
Test the `Graphics3D` function for rendering correct output:

```mma
Plot3D[x ^ 2 + 1 / y, {x, -1, 1}, {y, 1, 4}]
```

```mma
Plot3D[Sin[y + Sin[3 x]], {x, -2, 2}, {y, -2, 2}, PlotPoints->20]
```

```mma
Plot3D[x / (x ^ 2 + y ^ 2 + 1), {x, -2, 2}, {y, -2, 2}, Mesh->None]
```

```mma
Plot3D[Sin[x y] /(x y), {x, -3, 3}, {y, -3, 3}, Mesh->All]
```

```mma
Plot3D[Log[x + y^2], {x, -1, 1}, {y, -1, 1}]
```

```mma
SphericalPlot3D[{1, 2, 3}, {t, 0, Pi}, {p, 0, 3 Pi/2}]
```

```mma
DiscretePlot3D[PDF[MultivariatePoissonDistribution[3, {1, 1}], {t, u}], {t, 0, 10}, {u, 0, 10}]
```

```mma
ParametricPlot3D[{{4 + (3 + Cos[v]) Sin[u], 4 + (3 + Cos[v]) Cos[u],4 + Sin[v]}, {8 + (3 + Cos[v]) Cos[u], 3 + Sin[v], 4 + (3 + Cos[v]) Sin[u]}}, {u, 0, 2 Pi}, {v, 0, 2 Pi} ]
```

```mma
ParametricPlot3D[{{4 + (3 + Cos[v]) Sin[u], 4 + (3 + Cos[v]) Cos[u],4 + Sin[v]}, {8 + (3 + Cos[v]) Cos[u], 3 + Sin[v], 4 + (3 + Cos[v]) Sin[u]}}, {u, 0, 2 Pi}, {v, 0, 2 Pi},PlotStyle -> {Red, Green}]
```

```mma
RevolutionPlot3D[{2 + Cos[t], Sin[t]}, {t, 0, 2 Pi}]
```

```mma
ComplexPlot3D[(z^2 + 1)/(z^2 - 1), {z, -2 - 2 I, 2 + 2 I}]
```

```mma
ContourPlot3D[x^3 + y^2 - z^2 == 0, {x, -2, 2}, {y, -2, 2}, {z, -2, 2}]
```

```mma
Graphics3D[{Blue, Cylinder[], Red, Sphere[{0, 0, 2}], Black, Thick, 
  Dashed, Line[{{-2, 0, 2}, {2, 0, 2}, {0, 0, 4}, {-2, 0, 2}}], 
  Yellow, Polygon[{{-3, -3, -2}, {-3, 3, -2}, {3, 
     3, -2}, {3, -3, -2}}], Green, Opacity[.3], 
  Cuboid[{-2, -2, -2}, {2, 2, -1}]}]
```

```mma
Graphics3D[Sphere[{0, 0, 0}]]
```

```mma
Graphics3D[Cylinder[{{0, 0, 0}, {1, 1, 1}}, 1/2]]
```

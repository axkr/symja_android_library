
Test the Symja `Graphics3D` function and the 3D plot family.

Every block below has a counterpart in the Wolfram Language reference for the same symbol, so the
output can be compared side by side with the picture the reference shows.

## Plot3D

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
Plot3D[{x^2 + y^2, -x^2 - y^2}, {x, -2, 2}, {y, -2, 2}]
```

```mma
Plot3D[Sin[x y], {x, -2, 2}, {y, -2, 2}, ColorFunction->"Rainbow"]
```

```mma
Plot3D[Sin[x] Cos[y], {x, 0, 2 Pi}, {y, 0, 2 Pi}, PlotStyle->Red, Mesh->None]
```

```mma
Plot3D[x^2 - y^2, {x, -2, 2}, {y, -2, 2}, BoxRatios->{1, 1, 1}]
```

```mma
Plot3D[Sin[x + y], {x, -2, 2}, {y, -2, 2}, AxesLabel->{"x", "y", "z"}, PlotLabel->"a wave"]
```

```mma
Plot3D[x y, {x, -2, 2}, {y, -2, 2}, ViewPoint->{0, 0, 3}]
```

```mma
Plot3D[x y, {x, -2, 2}, {y, -2, 2}, Boxed->False, Axes->False]
```

## ParametricPlot3D

```mma
ParametricPlot3D[{Cos[t], Sin[t], t/3}, {t, 0, 6 Pi}]
```

```mma
ParametricPlot3D[{{4 + (3 + Cos[v]) Sin[u], 4 + (3 + Cos[v]) Cos[u],4 + Sin[v]}, {8 + (3 + Cos[v]) Cos[u], 3 + Sin[v], 4 + (3 + Cos[v]) Sin[u]}}, {u, 0, 2 Pi}, {v, 0, 2 Pi} ]
```

```mma
ParametricPlot3D[{{4 + (3 + Cos[v]) Sin[u], 4 + (3 + Cos[v]) Cos[u],4 + Sin[v]}, {8 + (3 + Cos[v]) Cos[u], 3 + Sin[v], 4 + (3 + Cos[v]) Sin[u]}}, {u, 0, 2 Pi}, {v, 0, 2 Pi},PlotStyle -> {Red, Green}]
```

```mma
ParametricPlot3D[{Cos[u] (2 + Cos[v]), Sin[u] (2 + Cos[v]), Sin[v]}, {u, 0, 2 Pi}, {v, 0, 2 Pi}, Mesh->None]
```

## SphericalPlot3D

```mma
SphericalPlot3D[1, {t, 0, Pi}, {p, 0, 2 Pi}]
```

```mma
SphericalPlot3D[{1, 2, 3}, {t, 0, Pi}, {p, 0, 3 Pi/2}]
```

```mma
SphericalPlot3D[Cos[2 t], {t, 0, Pi}, {p, 0, 2 Pi}]
```

## RevolutionPlot3D

```mma
RevolutionPlot3D[{2 + Cos[t], Sin[t]}, {t, 0, 2 Pi}]
```

```mma
RevolutionPlot3D[Sqrt[t], {t, 0, 4}]
```

```mma
RevolutionPlot3D[t^2, {t, 0, 2}, {a, 0, 3 Pi/2}]
```

## ContourPlot3D

```mma
ContourPlot3D[x^3 + y^2 - z^2 == 0, {x, -2, 2}, {y, -2, 2}, {z, -2, 2}]
```

```mma
ContourPlot3D[x^2 + y^2 + z^2 == 1, {x, -2, 2}, {y, -2, 2}, {z, -2, 2}]
```

```mma
ContourPlot3D[x^2 + y^2 + z^2, {x, -2, 2}, {y, -2, 2}, {z, -2, 2}, Contours->{1, 2}]
```

## ComplexPlot3D

```mma
ComplexPlot3D[(z^2 + 1)/(z^2 - 1), {z, -2 - 2 I, 2 + 2 I}]
```

```mma
ComplexPlot3D[Sin[z], {z, -2 - 2 I, 2 + 2 I}]
```

## List plots

```mma
ListPointPlot3D[N[Table[{Sin[t],Cos[t],Cos[t^2]},{t,500}]]]
```

```mma
ListPointPlot3D[Table[{i, j, i j}, {i, 5}, {j, 8}]]
```

```mma
ListPlot3D[Table[Sin[i/4] Cos[j/4], {i, 12}, {j, 16}]]
```

```mma
ListPlot3D[Table[{RandomReal[], RandomReal[], RandomReal[]}, {40}]]
```

```mma
ListLinePlot3D[Table[Sin[i/3] + Cos[j/3], {i, 8}, {j, 10}]]
```

```mma
DiscretePlot3D[Sin[i j], {i, 1, 10}, {j, 1, 10}]
```

```mma
DiscretePlot3D[i + j, {i, 1, 8}, {j, 1, 8}, ExtentSize->Full]
```

```mma
DiscretePlot3D[PDF[MultivariatePoissonDistribution[3, {1, 1}], {t, u}], {t, 0, 8}, {u, 0, 8}, ExtentSize->Full]
```

```mma
DiscretePlot3D[PDF[MultivariatePoissonDistribution[3, {1, 1}], {t, u}], {t, 0, 8}, {u, 0, 8}, ExtentSize->None]
```

## Primitives

```mma
Graphics3D[Sphere[{0, 0, 0}]]
```

```mma
Graphics3D[Cylinder[{{0, 0, 0}, {1, 1, 1}}, 1/2]]
```

```mma
Graphics3D[{Red, Cone[{{0, 0, 0}, {0, 0, 2}}, 1]}]
```

```mma
Graphics3D[{Blue, Cuboid[{0, 0, 0}, {1, 2, 3}]}]
```

```mma
Graphics3D[{Tetrahedron[], Translate[Cube[], {3, 0, 0}], Translate[Octahedron[], {6, 0, 0}]}]
```

```mma
Graphics3D[{Translate[Dodecahedron[], {-3, 0, 0}], Icosahedron[]}]
```

```mma
Graphics3D[Sphere[{{0, 0, 0}, {2, 0, 0}, {4, 0, 0}}, 0.8]]
```

```mma
Graphics3D[{Orange, Tube[{{0, 0, 0}, {1, 1, 1}, {2, 0, 1}, {3, 2, 0}}, 0.15]}]
```

```mma
Graphics3D[{Thick, BSplineCurve[{{0, 0, 0}, {1, 2, 0}, {2, -1, 1}, {3, 1, 2}, {4, 0, 0}}]}]
```

```mma
Graphics3D[{Blue, Polygon[{{0, 0, 0}, {1, 0, 0}, {1, 1, 1}, {0, 1, 0}}]}]
```

```mma
Graphics3D[{Text["origin", {0, 0, 0}], Text["far", {2, 2, 2}], Point[{{0, 0, 0}, {2, 2, 2}}]}]
```

```mma
Graphics3D[{Arrow[{{0, 0, 0}, {1, 1, 1}}], Red, Arrow[{{0, 0, 0}, {1, 0, 0}}], Blue, Arrow[{{0, 0, 0}, {0, 1, 0}}]}]
```

```mma
Graphics3D[GraphicsComplex[{{0, 0, 0}, {1, 0, 0}, {1, 1, 0}, {0, 1, 0}, {0.5, 0.5, 1}}, {Polygon[{{1, 2, 5}, {2, 3, 5}, {3, 4, 5}, {4, 1, 5}}]}, VertexColors -> {Red, Green, Blue, Yellow, White}]]
```

## Directives

```mma
Graphics3D[{Blue, Cylinder[], Red, Sphere[{0, 0, 2}], Black, Thick,
  Dashed, Line[{{-2, 0, 2}, {2, 0, 2}, {0, 0, 4}, {-2, 0, 2}}],
  Yellow, Polygon[{{-3, -3, -2}, {-3, 3, -2}, {3,
     3, -2}, {3, -3, -2}}], Green, Opacity[.3],
  Cuboid[{-2, -2, -2}, {2, 2, -1}]}]
```

```mma
Graphics3D[{Opacity[0.3], Red, Sphere[], Opacity[1], Blue, Sphere[{2, 0, 0}, 0.5]}]
```

```mma
Graphics3D[{Specularity[White, 40], Sphere[{0, 0, 0}], Sphere[{2.2, 0, 0}]}]
```

```mma
Graphics3D[{Glow[Red], Black, Sphere[]}]
```

```mma
Graphics3D[{EdgeForm[Black], Blue, Cuboid[]}]
```

```mma
Graphics3D[{EdgeForm[None], Blue, Cuboid[]}]
```

```mma
Graphics3D[{PointSize[0.02], Point[Table[{Cos[t], Sin[t], t/5}, {t, 0, 6 Pi, Pi/12}]]}]
```

```mma
Graphics3D[{Thickness[0.01], Red, Line[{{0, 0, 0}, {1, 1, 1}}], Blue, Dashing[{0.05, 0.05}], Line[{{0, 1, 0}, {1, 0, 1}}]}]
```

```mma
Graphics3D[{Directive[Red, Opacity[0.5]], Sphere[], Directive[Blue, Opacity[0.5]], Sphere[{1, 0, 0}]}]
```

```mma
Graphics3D[Style[Sphere[], Green]]
```

## Transformations

```mma
Graphics3D[{Cuboid[], Rotate[Cuboid[], Pi/4, {0, 0, 1}]}]
```

```mma
Graphics3D[{Cuboid[], Translate[Scale[Cuboid[], 0.5], {2, 0, 0}]}]
```

```mma
Graphics3D[{Cuboid[], GeometricTransformation[Cuboid[], {{1, 0, 0}, {0, 1, 0}, {0.5, 0, 1}}]}]
```

```mma
Graphics3D[GeometricTransformation[Cuboid[], {2, 0, 0}]]
```

## Sampling options

How much of the surface is drawn, and how finely.

```mma
Plot3D[Sin[10 x y], {x, 0, 1}, {y, 0, 1}, PlotPoints->12]
```

```mma
Plot3D[Sin[10 x y], {x, 0, 1}, {y, 0, 1}, PlotPoints->12, MaxRecursion->2]
```

```mma
Plot3D[x + y, {x, -2, 2}, {y, -2, 2}, RegionFunction->Function[{x, y, z}, x^2 + y^2 < 2]]
```

```mma
Plot3D[x + y, {x, -2, 2}, {y, -2, 2}, RegionFunction->Function[{x, y, z}, x^2 + y^2 < 2], BoundaryStyle->Red]
```

```mma
Plot3D[x y, {x, -2, 2}, {y, -2, 2}, Exclusions->{x == 0}]
```

```mma
Plot3D[1/(x y), {x, -2, 2}, {y, -2, 2}, ClippingStyle->None]
```

```mma
Plot3D[x + y, {x, 0, 1}, {y, 0, 1}, MeshFunctions->{Function[{x, y, z}, z]}, Mesh->10]
```

```mma
Plot3D[x^2 - y^2, {x, -1, 1}, {y, -1, 1}, MeshFunctions->{Function[{x, y, z}, x], Function[{x, y, z}, y]}, MeshStyle->Red]
```

## Legends

`PlotLegends` returns the graphic wrapped in `Legended`, which is what Wolfram returns.

```mma
Plot3D[{x + y, x - y}, {x, 0, 1}, {y, 0, 1}, PlotLegends->{"up", "down"}]
```

```mma
ListPointPlot3D[{{1, 1, 1}, {2, 2, 2}, {3, 1, 2}}, PlotLegends->Automatic]
```

## DiscretePlot3D

```mma
DiscretePlot3D[PDF[MultivariatePoissonDistribution[3, {1, 1}], {t, u}], {t, 0, 6}, {u, 0, 6}]
```

```mma
DiscretePlot3D[i + j, {i, 1, 5}, {j, 1, 5}, ExtentSize->Full]
```

```mma
DiscretePlot3D[i + j, {i, 1, 5}, {j, 1, 5}, ExtentSize->Scaled[0.5], PlotStyle->Red]
```

```mma
DiscretePlot3D[Sin[i] Cos[j], {i, 1, 6}, {j, 1, 6}, ExtentSize->None, Joined->True]
```

```mma
DiscretePlot3D[i + j, {i, 1, 4}, {j, 1, 4}, ExtentSize->None, PlotMarkers->"o"]
```

## ListPointPlot3D filling

```mma
ListPointPlot3D[Table[{i, j, Sin[i j]}, {i, 0, 3, 0.5}, {j, 0, 3, 0.5}], Filling->Bottom]
```

```mma
ListPointPlot3D[Table[{i, j, Sin[i j]}, {i, 0, 3, 0.5}, {j, 0, 3, 0.5}], Filling->Axis, FillingStyle->Red]
```

## ListLinePlot3D

```mma
ListLinePlot3D[{{1, 2, 3, 4, 5}, {2, 3, 4, 5, 6}, {3, 4, 5, 6, 7}}]
```

```mma
ListLinePlot3D[{{1, 2, 3, 4}, {5, 6, 7, 8}}, PlotStyle->{Red, Green}]
```

```mma
ListLinePlot3D[{{1, 2, 3, 4}, {5, 6, 7, 8}}, DataRange->{{0, 10}, {0, 20}}]
```

## Graph3D

```mma
Graph3D[Graph[{1 -> 2, 2 -> 3, 3 -> 1, 3 -> 4}]]
```

```mma
Graph3D[Graph[{1 -> 2, 2 -> 3, 3 -> 1, 3 -> 4}], VertexLabels->Automatic]
```

```mma
Graph3D[Graph[{1 -> 2, 2 -> 3, 3 -> 1, 3 -> 4}], VertexLabels->Automatic, EdgeLabels->"Name"]
```

```mma
Graph3D[Graph[{1 -> 2, 2 -> 3, 3 -> 4, 4 -> 5, 5 -> 1}], GraphLayout->"CircularEmbedding"]
```

```mma
Graph3D[Graph[{1 -> 2, 2 -> 3, 3 -> 4, 4 -> 5, 5 -> 6}], GraphLayout->"SpiralEmbedding"]
```

## Graphics3D options

```mma
Graphics3D[Sphere[], Axes->True]
```

```mma
Graphics3D[Sphere[], Axes->True, AxesLabel->{"x", "y", "z"}]
```

```mma
Graphics3D[Sphere[], Boxed->False]
```

```mma
Graphics3D[Sphere[], Axes->True, Boxed->False, Ticks->None]
```

```mma
Graphics3D[Cuboid[], BoxRatios->{1, 2, 3}]
```

```mma
Graphics3D[Sphere[], ViewPoint->{0, -3, 0}]
```

```mma
Graphics3D[Sphere[], ViewPoint->"Above"]
```

```mma
Graphics3D[Sphere[], ViewProjection->"Orthographic", Axes->True]
```

```mma
Graphics3D[Sphere[], Background->LightBlue]
```

```mma
Graphics3D[Sphere[], PlotLabel->"a sphere", ImageSize->250]
```

```mma
Graphics3D[Cuboid[], Axes->True, FaceGrids->All]
```

```mma
Graphics3D[{Sphere[{0, 0, 0}], Sphere[{2, 0, 0}]}, Lighting->"Neutral"]
```

```mma
Graphics3D[{Sphere[{0, 0, 0}], Sphere[{2, 0, 0}]}, Lighting->{{"Ambient", GrayLevel[0.4]}, {"Directional", Red, {2, 0, 2}}}]
```

```mma
Graphics3D[Sphere[], Lighting->None]
```

```mma
Graphics3D[Cuboid[], Axes->True, AxesEdge->{{-1, -1}, {-1, -1}, {-1, -1}}]
```

```mma
Legended[Graphics3D[Sphere[]], "a sphere"]
```

```mma
Plot3D[Exp[x + y], {x, 0, 2}, {y, 0, 2}, ScalingFunctions->{"Identity", "Identity", "Log"}]
```

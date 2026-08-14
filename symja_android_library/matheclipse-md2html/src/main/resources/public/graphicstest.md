
Test the Symja `Graphics` function.

```mma
Plot[Sin[x], {x, -Pi, Pi}]
```

```mma
Plot[Tan[x], {x, -Pi, Pi}, PlotRange->{-10,10}]
```

```mma
ParametricPlot[{Cos[u], Cos[2*u]}, {u, 0, 2 Pi}]
```

```mma
ParametricPlot[ r^2 { Sqrt[t]*Sin[t], Cos[t]}, {t, 0, 3 Pi/2}, {r, 1, 2}]
```

```mma
Plot[Sin[E^x],{x,-2,6},PlotRange->{-3,3}]
```

```mma
LogPlot[{x^x, Exp[x], x!}, {x, 1, 5}]
```

```mma
NumberLinePlot[{Prime[Range[20]],Prime[Range[40]],Prime[Range[80]]}]
```

```mma
LogLogPlot[{Log[x]^x, x^x}, {x, 0.1, 10}]
```

```mma
LogLinearPlot[{Erf[x], Erfc[x]}, {x, 0.01, 10}]
```

```mma
ListPolarPlot[Table[{n, Log[n]}, {n, 500}]]
```

```mma
ListPolarPlot[{Range[100]/4, Sqrt[Range[100]], Log[Range[100]]}]
```

```mma
ListPlot[Prime[Range[25]]]
```

```mma
ListPlot[
 Table[{k, 
   PDF[BinomialDistribution[50, p], k]}, {p, {0.3, 0.5, 0.8}}, {k, 0, 
   50}], Filling -> Axis]
```

```mma
ListPlot[Labeled[#, #] & /@ Table[n*3, {n, 10}],PlotStyle -> PointSize[Medium]]
```

```mma
ListLogLogPlot[{Range[20], Sqrt[Range[20]], Log[Range[20]]}, Joined -> True]
```

```mma
ListLogLogPlot[Range[20]^3, Filling -> Bottom]
```

```mma
ListLogLogPlot[Range[20]^3, Filling -> Axis]
```

```mma
DiscretePlot[MoebiusMu[k], {k, 1, 50}]
```

```mma
ListLogLinearPlot[ Table[{n, n^k}, {k, {-1, -0.5, 0.5, 1}}, {n, 1, 10}], Joined -> True ]
```

```mma
ContourPlot[Sin[x] + Sin[y], {x, 0, 4 Pi}, {y, 0, 4 Pi}]
```

```mma
DensityPlot[Cos[x]*Cos[y], {x, -6.5, 6.5}, {y, -6.5, 6.5}]
```

```mma
MatrixPlot[Inverse@ Table[Which[i==j, 2., i==j+1 || i==j-1, -1.,True,0.], {i,100},{j,100}]]
```

```mma
MatrixPlot[Table[Binomial[n, k], {n, 0, 25}, {k, 0, n}],Background -> Lighter[Yellow]]
```

```mma
ComplexPlot[(z^2 + 1)/(z^2 - 1), {z, -2 - 2*I, 2 + 2*I}, PlotLegends -> Automatic]
```

```mma
DensityHistogram[RandomVariate[BinormalDistribution[.5], 500],ChartLegends -> Automatic]
```

```mma
BoxWhiskerChart[ Table[RandomVariate[NormalDistribution[\[Mu], 1], 100], {\[Mu], {0, 3, 2, 5}}]]
```

```mma
Graphics[Point[Table[{t, Cos[t]}, {t,-Pi, Pi, 0.2}]]]
```

```mma
Graphics[ Table[{Hue[RandomReal[]], Arrow[RandomReal[1, {2, 2}]]}, {75}]]
```

```mma
Graphics[Table[{Hue[h, s], Disk[{12h, 8s}]}, {h, 0, 1, 1/6}, {s, 0, 1, 1/4}]]
```

```mma
Graphics[Table[{EdgeForm[{GrayLevel[0, 0.5]}], Hue[(-11+q+10*r)/72, 1, 1, 0.6], Disk[(8-r)*{Cos[2*Pi*q/12], Sin[2*Pi*q/12]}, (8-r)/3]}, {r,6}, {q, 12}]]
```

```mma
Graphics[Table[{AbsoluteThickness[t], Line[{{20 t, 10}, {20 t, 80}}], Text[ToString[t]<>"pt", {20 t, 0}]}, {t, 0, 10}]]
```

```mma
Graphics[GraphicsComplex[{{0, 0}, {2, 0}, {2, 2}, {0, 2}}, Table[Circle[i], {i, 4}]]]
```

```mma
data = Table[15 {Cos[t], Sin[t]}, {t, 0, 4*Pi, 4*Pi/5}];

Graphics[GraphicsComplex[data, {Green, Line[{1, 2, 3, 4, 5, 6}], Red, Point[{1, 2, 3, 4, 5}]}]]
```

```mma
Graphics[Line[{{-1, -1}, {3,3}, {1, 1}, {4, 5}}],Axes->True, PlotRange->{0.0, 2.0}]
```

```mma
TreeForm[a+(b*q*s)^(2*y)+Sin[c]^(3-z)]
```
## Primitives

```mma
Graphics[{Circle[{0, 0}, {2, 1}], Red, Circle[{0, 0}, 1, {0, Pi/2}], Blue, Disk[{4, 0}, 1, {0, 3 Pi/4}]}]
```

```mma
Graphics[{Annulus[{0, 0}, {1, 2}], Red, Annulus[{5, 0}, {1, 2}, {0, Pi}]}]
```

```mma
Graphics[Table[{Hue[n/8], RegularPolygon[{2 n, 0}, 1, n + 2]}, {n, 0, 6}]]
```

```mma
Graphics[{LightBlue, EdgeForm[Black], StadiumShape[{{0, 0}, {3, 1}}, 0.6]}]
```

```mma
Graphics[{Yellow, EdgeForm[Thick], Polygon[{{0, 0}, {4, 0}, {4, 4}, {0, 4}} -> {{{1, 1}, {2, 1}, {2, 2}, {1, 2}}}]}]
```

```mma
Graphics[{Raster[Table[N[Sin[i] Cos[j]], {i, 0, 6, 0.25}, {j, 0, 6, 0.25}]]}]
```

```mma
Graphics[{Red, HalfPlane[{{0, 0}, {1, 1}}, {1, -1}], Black, InfiniteLine[{{0, 0}, {1, 1}}]}]
```

```mma
Graphics[{Thick, JoinedCurve[{Line[{{0, 0}, {1, 1}}], BezierCurve[{{1, 1}, {2, 2}, {3, 0}, {4, 1}}]}]}]
```

```mma
Graphics[{Orange, FilledCurve[{Line[{{0, 0}, {2, 1}, {4, 0}, {2, -1}}]}]}]
```

## Transformations

```mma
Graphics[Table[Rotate[Rectangle[{0, 0}, {2, 1}], n Pi/6], {n, 0, 5}]]
```

```mma
Graphics[Translate[{Red, Disk[{0, 0}, 0.5]}, Table[{n, Sin[n]}, {n, 0, 6, 0.5}]]]
```

```mma
Graphics[{Scale[Rectangle[{0, 0}, {1, 1}], {3, 1}], Red, Scale[Rectangle[{0, 0}, {1, 1}], {1, 3}]}]
```

```mma
Graphics[GeometricTransformation[{Blue, Rectangle[{0, 0}, {2, 1}]}, RotationTransform[Pi/5]]]
```

## Directives

```mma
Graphics[Table[{Blend[{Red, Yellow, Green}, n/8], Rectangle[{n, 0}, {n + 0.9, 1}]}, {n, 0, 8}]]
```

```mma
Graphics[{Opacity[0.4], Red, Disk[{0, 0}, 1], Blue, Disk[{1, 0}, 1], Green, Disk[{0.5, 1}, 1]}]
```

```mma
Graphics[{Arrowheads[{-0.06, 0.06}], Arrow[{{0, 0}, {2, 1}}], Red, Arrowheads[Large], Arrow[{{0, 1}, {2, 2}}, 0.2]}]
```

```mma
Graphics[Table[Text[Style["size " <> ToString[n], FontSize -> n, Bold], {0, -n}], {n, 8, 20, 4}]]
```

```mma
Graphics[{Dashed, Line[{{0, 0}, {3, 0}}], Dotted, Line[{{0, 1}, {3, 1}}], DotDashed, Line[{{0, 2}, {3, 2}}]}]
```

## Options

```mma
Graphics[Disk[], Frame -> True, FrameLabel -> {"x axis", "y axis"}, PlotLabel -> "a disk in a frame"]
```

```mma
Graphics[Circle[], Axes -> True, GridLines -> Automatic, GridLinesStyle -> Directive[LightGray, Dashed]]
```

```mma
Graphics[{Red, Disk[]}, Frame -> True, Background -> LightYellow, ImageSize -> 250]
```

```mma
Graphics[Disk[{0, 0}, 3], PlotRange -> {{-1, 1}, {-1, 1}}, PlotRangeClipping -> True, Frame -> True]
```

```mma
Graphics[Line[{{0, 0}, {1, 1}}], Axes -> True, Ticks -> {{{0, "start"}, {1, "end"}}, Automatic}]
```

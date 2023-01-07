
Test the `Graphics` function for creating correct JSON output for the `Mathics 3D Graphics backend using three.js` project:
- [https://github.com/Mathics3/mathics-threejs-backend](https://github.com/Mathics3/mathics-threejs-backend)

```mma
Plot[Sin[x], {x, -Pi, Pi}]
```

```mma
Plot[Tan[x], {x, -Pi, Pi}, PlotRange->{-10,10}]
```

```mma
LogPlot[{x^x, Exp[x], x!}, {x, 1, 5}]
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
ListLogLogPlot[{Range[20], Sqrt[Range[20]], Log[Range[20]]}, Joined -> True]
```

```mma
ListLogLogPlot[Range[20]^3, Filling -> Bottom]
```
 
```mma
ListLogLinearPlot[ Table[{n, n^k}, {k, {-1, -0.5, 0.5, 1}}, {n, 1, 10}], Joined -> True ]
```

```mma
Graphics[Point[Table[{t, Cos[t]}, {t,-Pi, Pi, 0.2}]]]
```

```mma
Graphics[ Table[{Hue[RandomReal[]], Arrow[RandomReal[1, {2, 2}]]}, {75}]]
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

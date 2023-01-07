
Test the `Graphics3D` function for creating correct JSON output for the `json2D_JSXGraph` project:
- [https://github.com/jsxgraph/json2D_JSXGraph](https://github.com/jsxgraph/json2D_JSXGraph)

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

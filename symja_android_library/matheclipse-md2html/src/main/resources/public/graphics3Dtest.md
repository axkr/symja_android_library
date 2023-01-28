
Test the `Graphics` function for creating correct JSON output for the `Mathics 3D Graphics backend using three.js` project:
- [https://github.com/Mathics3/mathics-threejs-backend](https://github.com/Mathics3/mathics-threejs-backend)

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

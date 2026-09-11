## ListVectorPlot3D

```
ListVectorPlot3D(array)
```

> generates a 3D vector plot from an array of vector field values: `array[[i, j, k]]` is drawn at `{k, j, i}`.

```
ListVectorPlot3D({{{x1, y1, z1}, {vx1, vy1, vz1}}, {{x2, y2, z2}, {vx2, vy2, vz2}}, ...})
```

> generates a 3D vector plot with the vector `{vxi, vyi, vzi}` drawn at the point `{xi, yi, zi}`.

Each arrow is centred on its point and coloured by its length. The longest arrow spans `VectorScale` of the spacing between the points. Entries that are not numeric 3-vectors, and zero vectors, get no arrow.

See
* [Wikipedia - Vector field](https://en.wikipedia.org/wiki/Vector_field)

### Options

* `DataRange -> {{xmin, xmax}, {ymin, ymax}, {zmin, zmax}}` - spreads the array's positions over these ranges instead of `1, 2, 3, ...`
* `VectorScale -> s` - the fraction of the spacing between points the longest arrow spans (default `0.9`)
* `VectorColorFunction -> None` - draws all arrows in one colour

Any other option is handed on to the `Graphics3D`.

### Examples

```
>> ListVectorPlot3D(Table({y, -x, z}, {z, -1, 1, 0.5}, {y, -1, 1, 0.5}, {x, -1, 1, 0.5}))

>> ListVectorPlot3D(Table({y, -x, z}, {z, -1, 1, 0.5}, {y, -1, 1, 0.5}, {x, -1, 1, 0.5}), DataRange -> {{-1, 1}, {-1, 1}, {-1, 1}})

>> ListVectorPlot3D({{{0, 0, 0}, {1, 0, 0}}, {{1, 1, 1}, {0, 0, 1}}, {{2, 0, 1}, {1, 1, 0}}})
```

### Related terms
[VectorPlot3D](VectorPlot3D.md)

### Implementation status

* &#x1F9EA; - experimental

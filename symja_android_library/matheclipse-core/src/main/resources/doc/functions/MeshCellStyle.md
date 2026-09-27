## MeshCellStyle

```
MeshCellStyle -> spec
```

> an option of a mesh region, giving the style its cells are drawn in.

| spec | cells styled |
| --- | --- |
| `style` | every cell of every dimension |
| `{d, All} -> style` | every cell of dimension `d` |
| `{d, i} -> style` | the `i`th cell of dimension `d` |
| `{rule1, rule2, ...}` | each rule in turn; a later one wins for the same cell |

Dimension `0` is the vertices, `1` the edges and `2` the faces. A two dimensional region has one
face, the area its boundary encloses, even though only its edges are stored.

The setting is not kept as it was given. As in the reference implementation it is written out cell
by cell into a `Properties` option, each dimension closed by `{d, Default} -> MeshCellStyle ->
Automatic`:

```
>> ConvexHullMesh({{0,0},{2,0},{2,2},{0,2}}, MeshCellStyle -> {{1, 2} -> Red})
BoundaryMeshRegion({{0,0},{2,0},{2,2},{0,2}},{Line({{1,2},{2,3},{3,4},{4,1}})},Properties->{{1,2}->MeshCellStyle->RGBColor(1,0,0),{1,Default}->MeshCellStyle->Automatic},Method->{SeparateBoundaries->False},WorkingPrecision->Infinity)
```

When the region is drawn, the styled cells are drawn over it in their style. A styled face replaces
the face beneath it, so a translucent one stays translucent:

```
>> Show(ConvexHullMesh({{0,0,0},{1,0,0},{0,1,0},{0,0,1}}, MeshCellStyle -> {{2, All} -> Opacity(0.5, LightBlue)}))
```

### Related terms
[ConvexHullMesh](ConvexHullMesh.md), [Show](Show.md)

## Show

```
Show(g1, g2, ..., options)
```

> shows the graphics together, as one picture.

```
Show(mesh)
```

> shows a mesh region as the `Graphics` or `Graphics3D` it is drawn as.

Each graphic keeps its primitives in a list of its own, so that one's colours and other directives
do not reach into the next. Options are merged with the first setting of each winning, and the
options given to `Show` itself come first.

### Examples

```
>> Show(Graphics({Red, Disk()}), Graphics({Blue, Point({0,0})}), Axes -> True)

>> Show(ConvexHullMesh({{0,0},{2,0},{2,2},{0,2}}))
```

`Show` does not join the plot ranges of the graphics it combines, so the first graphic's range can
clip the others; it does not merge `Epilog` or `Prolog`; and it does not combine two and three
dimensional graphics.

### Related terms
[ConvexHullMesh](ConvexHullMesh.md), [Graphics](Graphics.md), [Graphics3D](Graphics3D.md)

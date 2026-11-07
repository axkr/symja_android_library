## ConstantRegionQ

```
ConstantRegionQ(reg)
```

> returns `True` if `reg` is a region which doesn't depend on a parameter, and `False` otherwise.

A region is constant if none of the values which describe it is a symbol: `Disk({0,0}, 2)` is constant, `Disk({0,0}, r)` is not. The variables of an `ImplicitRegion` or a `ParametricRegion` are bound by the region, so only the other symbols in its formula count as parameters. Combinations of regions, like `RegionUnion`, are constant if their parts are. An expression which isn't a region gives `False`.

### Examples

```
>> ConstantRegionQ(Disk({0,0},2))
True

>> ConstantRegionQ(Disk({0,0},r))
False

>> ConstantRegionQ(Triangle({{0,0},{1,0},{0,1}}))
True

>> ConstantRegionQ(ConvexHullMesh({{0,0},{2,0},{2,2},{0,2}}))
True
```

The variables `x` and `y` are bound by the region, `r` is a parameter:

```
>> ConstantRegionQ(ImplicitRegion(x^2+y^2<=1, {x,y}))
True

>> ConstantRegionQ(ImplicitRegion(x^2+y^2<=r, {x,y}))
False
```

```
>> ConstantRegionQ(RegionUnion(Disk(), Disk({3,0},1)))
True

>> ConstantRegionQ(42)
False
```

### Related terms
[ConvexRegionQ](ConvexRegionQ.md), [MeshRegionQ](MeshRegionQ.md), [RegionMember](RegionMember.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of ConstantRegionQ](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/ConstantRegionQ.java)

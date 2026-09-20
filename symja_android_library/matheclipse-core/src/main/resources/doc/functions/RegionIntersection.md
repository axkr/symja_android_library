## RegionIntersection

```
RegionIntersection(reg1, reg2, ...)
```

> the region of the points which lie in every one of `reg1, reg2, ...`.

Every region has to live in the same space - `RegionEmbeddingDimension` has to agree - or the parts
do not describe one region, which is reported as `RegionIntersection::regdims`.

The intersection is worked out where it is certainly known:

| | |
| --- | --- |
| the same region twice | that region |
| `EmptyRegion(n)` among them | `EmptyRegion(n)` |
| `FullRegion(n)` among them | the others |
| axis-aligned boxes | the box they overlap in, or `EmptyRegion(n)` |
| balls or disks about one centre | the smallest of them |
| `ImplicitRegion`s over the same variables | one `ImplicitRegion` of their conditions |
| `Interval`s of the line | the interval they overlap in |
| regions whose bounds lie apart | `EmptyRegion(n)` |

Anything else stays as it was written, which is not a failure: the unevaluated form is itself a
region, and [RegionMember](RegionMember.md) reads it as the conjunction of its parts.

### Examples

```
>> RegionIntersection(Rectangle({0,0},{2,2}), Rectangle({1,1},{3,3}))
Rectangle({1,1},{2,2})

>> RegionIntersection(Rectangle({0,0},{1,1}), Rectangle({5,5},{6,6}))
EmptyRegion(2)

>> RegionIntersection(Ball({0,0,0},2), Ball({0,0,0},1))
Ball({0,0,0},1)

>> RegionIntersection(Disk({0,0},1), Disk({9,9},1))
EmptyRegion(2)

>> RegionIntersection(Interval({0,3}), Interval({1,5}))
Interval({1,3})

>> RegionIntersection(ImplicitRegion(x^2+y^2<1,{x,y}), ImplicitRegion(x>0,{x,y}))
ImplicitRegion(x^2+y^2<1&&x>0,{x,y})

>> Area(RegionIntersection(Rectangle({0,0},{2,2}), Rectangle({1,1},{3,3})))
1
```

Two disks that are not about one centre cannot be written as one shape, so the intersection stays
as it is - and still answers which points lie in both:

```
>> RegionMember(RegionIntersection(Disk({0,0},1), Disk({1,0},1)), {1/2,0})
True

>> RegionMember(RegionIntersection(Disk({0,0},1), Disk({3,0},1)), {1/2,0})
False
```

### Related terms
[EmptyRegion](EmptyRegion.md), [FullRegion](FullRegion.md), [ImplicitRegion](ImplicitRegion.md), [RegionMember](RegionMember.md)

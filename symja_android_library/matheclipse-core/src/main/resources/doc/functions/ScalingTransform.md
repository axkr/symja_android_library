## ScalingTransform

```
ScalingTransform({s1, s2, ...})
```

> gives a `TransformationFunction` that scales by the factor `s1` along the first coordinate axis, by `s2` along the second and so on.

```
ScalingTransform({s1, s2, ...}, p)
```

> gives a scaling along the coordinate axes that leaves the point `p` fixed.

```
ScalingTransform(s, v)
```

> gives a scaling by the factor `s` along the direction of the vector `v`.

```
ScalingTransform(s, v, p)
```

> gives a scaling along the direction of the vector `v` that leaves the point `p` fixed.

`ScalingTransform` works in any number of dimensions. Directions perpendicular to `v` are left 
unchanged by `ScalingTransform(s, v)`.

See
* [Wikipedia - Scaling (geometry)](https://en.wikipedia.org/wiki/Scaling_(geometry))
* [Wikipedia - Transformation matrix](https://en.wikipedia.org/wiki/Transformation_matrix)

### Examples

Scale along the coordinate axes:

```
>> ScalingTransform({2, 3})
TransformationFunction({{2,0,0},{0,3,0},{0,0,1}})
```

```
>> ScalingTransform({2, 3})[{1, 1}]
{2,3}
```

The scaling factors may be symbolic:

```
>> ScalingTransform({sx, sy})
TransformationFunction({{sx,0,0},{0,sy,0},{0,0,1}})
```

Leave the point `{1, 1}` fixed:

```
>> ScalingTransform({2, 3}, {1, 1})
TransformationFunction({{2,0,-1},{0,3,-2},{0,0,1}})
```

Scale by the factor `2` along the diagonal direction `{1, 1}`:

```
>> ScalingTransform(2, {1, 1})
TransformationFunction({{3/2,1/2,0},{1/2,3/2,0},{0,0,1}})
```

Directional scaling works in any number of dimensions:

```
>> ScalingTransform(3, {1, 2, 2})
TransformationFunction({{11/9,4/9,4/9,0},{4/9,17/9,8/9,0},{4/9,8/9,17/9,0},{0,0,
0,1}})
```

A directional scaling which leaves the point `{1, 1}` fixed:

```
>> ScalingTransform(2, {1, 1}, {1, 1})[{1, 1}]
{1,1}
```

### Related terms
[AffineTransform](AffineTransform.md), [ReflectionTransform](ReflectionTransform.md), 
[RotationTransform](RotationTransform.md), [ShearingTransform](ShearingTransform.md), 
[TransformationFunction](TransformationFunction.md), [TranslationTransform](TranslationTransform.md)

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of ScalingTransform](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/TensorFunctions.java#L1787) 

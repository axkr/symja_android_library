## ShearingTransform

```
ShearingTransform(phi, u, n)
```

> gives a `TransformationFunction` that shears by the angle `phi` in the direction of the vector `u`, normal to the vector `n` and leaves the origin fixed.

```
ShearingTransform(phi, u, n, p)
```

> gives a shear that leaves the point `p` fixed.

`ShearingTransform` works in any number of dimensions and always gives an area- or volume-preserving 
transformation. `ShearingTransform(phi, {1, 0}, {0, 1})` is a horizontal shear by the angle `phi`, 
`ShearingTransform(phi, {0, 1}, {1, 0})` a vertical one. The inverse of 
`ShearingTransform(phi, u, n)` is `ShearingTransform(-phi, u, n)`.

See
* [Wikipedia - Shear mapping](https://en.wikipedia.org/wiki/Shear_mapping)
* [Wikipedia - Transformation matrix](https://en.wikipedia.org/wiki/Transformation_matrix)

### Examples

A horizontal shear:

```
>> ShearingTransform(phi, {1, 0}, {0, 1})
TransformationFunction({{1,Tan(phi),0},{0,1,0},{0,0,1}})
```

```
>> ShearingTransform(Pi/4, {1, 0}, {0, 1})
TransformationFunction({{1,1,0},{0,1,0},{0,0,1}})
```

A vertical shear which leaves the point `{1, 1}` fixed:

```
>> ShearingTransform(Pi/6, {0, 1}, {1, 0}, {1, 1})
TransformationFunction({{1,0,0},{1/Sqrt(3),1,-1/Sqrt(3)},{0,0,1}})
```

Shearing a deck of cards in 3 dimensions:

```
>> ShearingTransform(Pi/4, {1, 0, 0}, {0, 1, 0}, {1, 2, 3})
TransformationFunction({{1,1,0,-2},{0,1,0,0},{0,0,1,0},{0,0,0,1}})
```

The shear is undone by its inverse:

```
>> InverseFunction(ShearingTransform(Pi/4, {1, 0}, {0, 1}))[{2, 1}]
{1,1}
```

### Related terms
[AffineTransform](AffineTransform.md), [ReflectionTransform](ReflectionTransform.md), 
[RotationTransform](RotationTransform.md), [ScalingTransform](ScalingTransform.md), 
[TransformationFunction](TransformationFunction.md), [TranslationTransform](TranslationTransform.md)

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of ShearingTransform](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/TensorFunctions.java#L1930) 

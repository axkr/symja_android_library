## TransformationFunction

```
TransformationFunction(m)
```

> represents a transformation defined by the homogeneous transformation matrix `m`.
 
A `TransformationFunction` can be applied to a vector. Two transformations can be combined with 
`Dot(...)` or `Composition(...)`, the inverse transformation is given by `InverseFunction(...)`.

See
* [Wikipedia - Transformation matrix](https://en.wikipedia.org/wiki/Transformation_matrix)

### Examples

```
>> RotationTransform(Pi).TranslationTransform({1, -1})
TransformationFunction({{-1,0,-1},{0,-1,1},{0,0,1}})
 
>> TranslationTransform({1, -1}).RotationTransform(Pi)
TransformationFunction({{-1,0,1},{0,-1,-1},{0,0,1}})
```

`Composition(f, g)` first applies `g` and then `f`:

```
>> Composition(TranslationTransform({1, 2}), RotationTransform(Pi/2))
TransformationFunction({{0,-1,1},{1,0,2},{0,0,1}})

>> Composition(TranslationTransform({1, 2}), RotationTransform(Pi/2))[{1, 0}]
{1,3}
```

The inverse transformation:

```
>> InverseFunction(TranslationTransform({a, b}))
TransformationFunction({{1,0,-a},{0,1,-b},{0,0,1}})

>> InverseFunction(TranslationTransform({1, 2}))[{5, 5}]
{4,3}
```

### Related terms
[AffineTransform](AffineTransform.md), [ReflectionTransform](ReflectionTransform.md), 
[RotationTransform](RotationTransform.md), [ScalingTransform](ScalingTransform.md), 
[ShearingTransform](ShearingTransform.md), [TranslationTransform](TranslationTransform.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of TransformationFunction](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/TensorFunctions.java#L1981)

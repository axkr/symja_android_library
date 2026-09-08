## RotationTransform

```
RotationTransform(phi)
```

> gives a rotation by `phi`

```
RotationTransform(phi, p)
```

> gives a rotation by `phi`  around the point `p`.

See
* [Wikipedia - Rotation (mathematics)](https://en.wikipedia.org/wiki/Rotation_(mathematics))

### Examples


```
>> RotationTransform(Pi).TranslationTransform({1, -1})
TransformationFunction({{-1,0,-1},{0,-1,1},{0,0,1}})
 
>> TranslationTransform({1, -1}).RotationTransform(Pi)
TransformationFunction({{-1,0,1},{0,-1,-1},{0,0,1}})
```

Rotate around the point `{1, 0}`:

```
>> RotationTransform(Pi/2, {1, 0})
TransformationFunction({{0,-1,1},{1,0,-1},{0,0,1}})
```

### Related terms
[AffineTransform](AffineTransform.md), [ReflectionTransform](ReflectionTransform.md), 
[ScalingTransform](ScalingTransform.md), [ShearingTransform](ShearingTransform.md), 
[TransformationFunction](TransformationFunction.md), [TranslationTransform](TranslationTransform.md)

## TransformationFunction

```
TransformationFunction(m)
```

> represents a transformation.

```
TransformationFunction(phi, {0, 1}, {1, 0})
```

> gives a vertical shear by the angle `phi`.
 

See
* [Wikipedia - Transformation matrix](https://en.wikipedia.org/wiki/Transformation_matrix)

### Examples

```
>> RotationTransform(Pi).TranslationTransform({1, -1})
TransformationFunction(
{{-1,0,-1},
 {0,-1,1},
 {0,0,1}})
 
>> TranslationTransform({1, -1}).RotationTransform(Pi)
TransformationFunction(
{{-1,0,1},
 {0,-1,-1},
 {0,0,1}})
```

### Related terms
[RotationTransform](RotationTransform.md), [TranslationTransform](TranslationTransform.md)

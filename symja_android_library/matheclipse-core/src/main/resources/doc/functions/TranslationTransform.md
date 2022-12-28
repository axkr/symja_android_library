## TranslationTransform

```
TranslationTransform(v)
```

> gives a `TransformationFunction` that translates points by vector `v`. 

See
* [Wikipedia - Transformation matrix](https://en.wikipedia.org/wiki/Transformation_matrix)

### Examples
 
```
>> TranslationTransform({1, 2})
TransformationFunction(
{{1,0,1},
 {0,1,2},
 {0,0,1}})
```

### Related terms
[RotationTransform](RotationTransform.md), [TransformationFunction](TransformationFunction.md)
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
TransformationFunction({{1,0,1},{0,1,2},{0,0,1}})
```

```
>> TranslationTransform({x0, y0})[{x, y}]
{x+x0,y+y0}
```

### Related terms
[AffineTransform](AffineTransform.md), [ReflectionTransform](ReflectionTransform.md), 
[RotationTransform](RotationTransform.md), [ScalingTransform](ScalingTransform.md), 
[ShearingTransform](ShearingTransform.md), [TransformationFunction](TransformationFunction.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of TranslationTransform](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/TensorFunctions.java#L2023) 

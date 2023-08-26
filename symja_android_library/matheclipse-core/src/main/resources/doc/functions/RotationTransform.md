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
[TransformationFunction](TransformationFunction.md), [TranslationTransform](TranslationTransform.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of RotationTransform](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/TensorFunctions.java#L1032) 

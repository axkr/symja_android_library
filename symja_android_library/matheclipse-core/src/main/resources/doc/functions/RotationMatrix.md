## RotationMatrix

```
RotationMatrix(theta)
```

> yields a rotation matrix for the angle `theta`.

See
* [Wikipedia - Rotation matrix](https://en.wikipedia.org/wiki/Rotation_matrix)

### Examples

```
>> RotationMatrix(90*Degree)
{{0,-1},{1,0}}

>> RotationMatrix(t,{1,0,0})
{{1,0,0},{0,Cos(t),-Sin(t)},{0,Sin(t),Cos(t)}}
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of RotationMatrix](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/VectorAnalysisFunctions.java#L226) 

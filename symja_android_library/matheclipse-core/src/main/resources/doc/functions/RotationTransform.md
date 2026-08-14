## RotationTransform

```
RotationTransform(phi)
```

> gives a rotation by `phi`

```
RotationTransform(phi, p)
```

> gives a rotation by `phi`  around the 2D point `p`.

```
RotationTransform(phi, w)
```

> gives a 3D rotation by `phi` around the direction of the 3D vector `w`.

```
RotationTransform(phi, w, p)
```

> gives a 3D rotation by `phi` around the axis `w` anchored at the point `p`.

```
RotationTransform({u, v})
```

> gives a rotation about the origin which transforms the vector `u` into the direction of the vector `v`.

```
RotationTransform({u, v}, p)
```

> gives that rotation about the point `p`.

```
RotationTransform(phi, {u, v})

RotationTransform(phi, {u, v}, p)
```

> gives a rotation by `phi` in the plane spanned by the vectors `u` and `v`, about the origin or about the point `p`.

See
* [Wikipedia - Rotation (mathematics)](https://en.wikipedia.org/wiki/Rotation_(mathematics))
* [Wikipedia - Rodrigues' rotation formula](https://en.wikipedia.org/wiki/Rodrigues%27_rotation_formula)

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

The 3D rotation around an axis is represented by a `4x4` homogeneous matrix, so that it
composes with the other 3D transformations. The axis doesn't have to be normalized:

```
>> RotationTransform(Pi/6, {0, 0, 1})
TransformationFunction({{Sqrt(3)/2,-1/2,0,0},{1/2,Sqrt(3)/2,0,0},{0,0,1,0},{0,0,0,1}})
 
>> RotationTransform(Pi/6, {0, 0, 1})[{1, 0, 0}]
{Sqrt(3)/2,1/2,0}

>> RotationTransform(Pi/2, {0, 0, 2})[{1, 0, 0}]
{0,1,0}
```

The point `p` is left fixed by the rotation around the axis anchored at `p`:

```
>> RotationTransform(Pi/2, {0, 0, 1}, {1, 0, 0})[{1, 0, 0}]
{1,0,0}
```

A pair of vectors gives the rotation which maps the direction of the first vector onto 
the direction of the second one:

```
>> RotationTransform({{1, 0}, {0, 1}})[{1, 0}]
{0,1}

>> RotationTransform({{1, 0}, {0, 1}}, {1, 1})
TransformationFunction({{0,-1,2},{1,0,0},{0,0,1}})
```

With an angle the pair spans the rotation plane, which specifies any rotation in any 
number of dimensions. The 2D rotation is the rotation in the plane of the coordinate 
axes and the rotation around an axis is the rotation in the plane orthogonal to it:

```
>> RotationTransform(t) === RotationTransform(t, {{1, 0}, {0, 1}})
True

>> RotationTransform(t, {{1, 0, 0}, {0, 1, 0}}) === RotationTransform(t, {0, 0, 1})
True

>> RotationTransform(Pi/2, {{1, 0, 0}, {0, 0, 1}})[{1, 0, 0}]
{0,0,1}
```

### Related terms
[TransformationFunction](TransformationFunction.md), [TranslationTransform](TranslationTransform.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of RotationTransform](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/TensorFunctions.java#L1297) 

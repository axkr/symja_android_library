## RotationMatrix

```
RotationMatrix(theta)
```

> yields a 2D rotation matrix which rotates counterclockwise by the angle `theta`.

```
RotationMatrix(theta, w)
```

> yields a 3D rotation matrix for a counterclockwise rotation by the angle `theta` around the 3D vector `w`.

```
RotationMatrix({u, v})
```

> yields the matrix which rotates the vector `u` into the direction of the vector `v`.

```
RotationMatrix(theta, {u, v})
```

> yields the matrix which rotates by the angle `theta` in the plane spanned by the vectors `u` and `v`.

See
* [Wikipedia - Rotation matrix](https://en.wikipedia.org/wiki/Rotation_matrix)
* [Wikipedia - Rodrigues' rotation formula](https://en.wikipedia.org/wiki/Rodrigues%27_rotation_formula)

### Examples

```
>> RotationMatrix(90*Degree)
{{0,-1},{1,0}}

>> RotationMatrix(t,{1,0,0})
{{1,0,0},{0,Cos(t),-Sin(t)},{0,Sin(t),Cos(t)}}
```

The rotation axis doesn't have to be parallel to a coordinate axis and doesn't have to 
be normalized:

```
>> RotationMatrix(t,{1,1,1})
{{1/3*(1+2*Cos(t)),1/3*(1-Cos(t)-Sqrt(3)*Sin(t)),1/3*(1-Cos(t)+Sqrt(3)*Sin(t))},{1/3*(1-Cos(t)+Sqrt(3)*Sin(t)),1/3*(1+2*Cos(t)),1/3*(1-Cos(t)-Sqrt(3)*Sin(t))},{1/3*(1-Cos(t)-Sqrt(3)*Sin(t)),1/3*(1-Cos(t)+Sqrt(3)*Sin(t)),1/3*(1+2*Cos(t))}}

>> RotationMatrix(Pi/2,{0,0,3})
{{0,-1,0},{1,0,0},{0,0,1}}
```

The rotation leaves its axis fixed:

```
>> Simplify(RotationMatrix(t,{1,1,1}) . {1,1,1})
{1,1,1}
```

A pair of vectors `{u, v}` rotates `u` into the direction of `v` and leaves the lengths 
and the orthogonal complement of the plane unchanged:

```
>> RotationMatrix({{1,0},{3,4}})
{{3/5,-4/5},{4/5,3/5}}

>> Simplify(RotationMatrix({{2,3,6},{0,0,1}}) . {2,3,6})
{0,0,7}
```

With an angle the pair spans the rotation plane; a positive angle rotates from `u` 
towards `v`. This works in any number of dimensions:

```
>> RotationMatrix(t,{{1,0,0},{0,0,1}})
{{Cos(t),0,-Sin(t)},{0,1,0},{Sin(t),0,Cos(t)}}

>> RotationMatrix(t,{{1,0,0,0},{0,1,0,0}})
{{Cos(t),-Sin(t),0,0},{Sin(t),Cos(t),0,0},{0,0,1,0},{0,0,0,1}}
```

The rotation around an axis is the rotation in the plane orthogonal to it, so both 
2-argument forms describe the same rotations. The plane spanned by `{1,1,1}` and 
`{1,-2,1}` has the normal `{1,0,-1}`:

```
>> Simplify(RotationMatrix(t,{{1,1,1},{1,-2,1}}) - RotationMatrix(t,{1,0,-1}))
{{0,0,0},{0,0,0},{0,0,0}}
```

Rotation matrices are orthogonal with determinant `1`, so the inverse is the transpose 
and the norm of a rotated vector doesn't change. For complex vectors the matrix is 
unitary:

```
>> Simplify(Inverse(RotationMatrix(t,{1,2,3})) - Transpose(RotationMatrix(t,{1,2,3})))
{{0,0,0},{0,0,0},{0,0,0}}

>> Simplify(Det(RotationMatrix(t,{{1,1,1},{1,-2,1}})))
1

>> Simplify(ConjugateTranspose(RotationMatrix({{1,I,0},{0,1,I}})) . RotationMatrix({{1,I,0},{0,1,I}}))
{{1,0,0},{0,1,0},{0,0,1}}
```

Two vectors which don't span a plane don't determine a rotation. Only the identity and 
the point reflection in 2D are independent of the undetermined plane, all other cases 
stay unevaluated:

```
>> RotationMatrix({{1,0},{2,0}})
{{1,0},{0,1}}

>> RotationMatrix({{1,0},{-1,0}})
{{-1,0},{0,-1}}

>> RotationMatrix({{1,0,0},{-1,0,0}})
RotationMatrix({{1,0,0},{-1,0,0}})
```

The option `TargetStructure` isn't supported, because `Symja` has no structured arrays.

### Related terms
[RotationTransform](RotationTransform.md)






### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of RotationMatrix](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/VectorAnalysisFunctions.java#L1204) 

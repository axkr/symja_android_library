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

## AASTriangle

```
AASTriangle(alpha, beta, a)
```

> returns a triangle from 2 angles `alpha`, `beta` and side `a` (which is not between the angles).
  

See:
* [Wikipedia - Solution of triangles](https://en.wikipedia.org/wiki/Solution_of_triangles)
 

### Examples

```
>> AASTriangle(Pi/2, Pi/3, b) 
Triangle({{0,0},{b/2,0},{0,1/2*Sqrt(3)*b}})

>> ASATriangle(Pi/2, b, Pi/3)
Triangle({{0,0},{b,0},{0,Sqrt(3)*b}})
```

### Related terms 
[ASATriangle](ASATriangle.md), [SASTriangle](SASTriangle.md), [SSSTriangle](SSSTriangle.md)







### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of AASTriangle](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ComputationalGeometryFunctions.java#L52) 

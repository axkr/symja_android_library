## SASTriangle

```
SASTriangle(a, gamma, b)
```

> returns a triangle from 2 sides `a`, `b` and angle `gamma` (which is between the sides).
  

See:
* [Wikipedia - Solution of triangles](https://en.wikipedia.org/wiki/Solution_of_triangles)
 

### Examples

``` 
>> SASTriangle(1, Pi/3, 1)
Triangle({{0,0},{1,0},{1/2,Sqrt(3)/2}})
```

### Related terms 
[AASTriangle](AASTriangle.md), [ASATriangle](ASATriangle.md), [SSSTriangle](SSSTriangle.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of SASTriangle](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ComputationalGeometryFunctions.java#L114) 

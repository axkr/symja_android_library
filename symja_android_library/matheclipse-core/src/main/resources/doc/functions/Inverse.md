## Inverse

```
Inverse(matrix)
```

> computes the inverse of the `matrix`. 

See:  
* [Wikipedia - Invertible matrix](https://en.wikipedia.org/wiki/Invertible_matrix)
* [Youtube - Inverse matrices, column space and null space | Essence of linear algebra, chapter 7](https://youtu.be/uQhTuRlWMxw)

### Examples

```
>> Inverse({{1, 2, 0}, {2, 3, 0}, {3, 4, 1}})
{{-3,2,0},
 {2,-1,0},
 {1,-2,1}}
```

The matrix `{{1, 0}, {0, 0}}` is singular.

```
>> Inverse({{1, 0}, {0, 0}}) 
Inverse({{1, 0}, {0, 0}})

>> Inverse({{1, 0, 0}, {0, Sqrt(3)/2, 1/2}, {0,-1 / 2, Sqrt(3)/2}})
{{1,0,0},
 {0,Sqrt(3)/2,-1/2},
 {0,1/2,1/(1/(2*Sqrt(3))+Sqrt(3)/2)}} 
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Inverse](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/LinearAlgebra.java#L2910) 

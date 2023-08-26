## Orthogonalize

```
Orthogonalize(matrix)
```

> returns a basis for the orthogonalized set of vectors defined by `matrix`.
 
See
* [Wikipedia - Gram–Schmidt process (linear algebra)](https://en.wikipedia.org/wiki/Gram%E2%80%93Schmidt_process)
* [Wikipedia - Orthogonal_matrix](https://en.wikipedia.org/wiki/Orthogonal_matrix)

### Examples

```
>> Orthogonalize({{3,1},{2,2}})
{{3/Sqrt(10),1/Sqrt(10)},{-Sqrt(5/2)/5,3/5*Sqrt(5/2)}}
```
 






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Orthogonalize](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/LinearAlgebra.java#L4350) 

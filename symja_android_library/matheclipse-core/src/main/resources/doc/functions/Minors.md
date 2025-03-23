## Minors

```
Minors(matrix) 
```

> returns the minors of the matrix.


```
Minors(matrix, n) 
```

> returns the minors of the matrix with the determinant of the minors of size  `n` x `n`

```
Minors(matrix, n, function) 
```

> returns the minors of the matrix with the function applied on the minors of size  `n` x `n`

See:
* [Wikipedia - Minor (linear algebra)](https://en.wikipedia.org/wiki/Minor_(linear_algebra))
	
### Examples

```
>> Minors({{1,4,7},{3,0,5},{-1,9,11}})
{{-12,-16,20},{13,18,-19},{27,38,-45}}
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Minors](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/LinearAlgebra.java#L4288) 

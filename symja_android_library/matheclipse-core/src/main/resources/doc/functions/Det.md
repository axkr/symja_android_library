## Det

```
Det(matrix)
```

> computes the determinant of the `matrix`.

See:
* [Wikipedia: Determinant](https://en.wikipedia.org/wiki/Determinant)
* [Youtube - The determinant | Essence of linear algebra, chapter 6](https://youtu.be/Ip3X9LOh2dk)
* [Youtube - Cramer's rule, explained geometrically | Essence of linear algebra, chapter 12](https://youtu.be/jBsC34PxzoM)

### Examples

```
>> Det({{1, 1, 0}, {1, 0, 1}, {0, 1, 1}})
-2
```

Symbolic determinant:

```
>> Det({{a, b, c}, {d, e, f}, {g, h, i}})
-c*e*g+b*f*g+c*d*h-a*f*h-b*d*i+a*e*i 
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Det](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/LinearAlgebra.java#L1447) 

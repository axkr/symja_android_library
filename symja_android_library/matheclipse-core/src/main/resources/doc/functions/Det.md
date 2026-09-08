## Det

```
Det(matrix)
```

> computes the determinant of the `matrix`.

See:
* [Wikipedia - Determinant](https://en.wikipedia.org/wiki/Determinant)
* [Wikipedia - Row echelon form](https://en.wikipedia.org/wiki/Row_echelon_form)
* [Wikipedia - Laplace expansion](https://en.wikipedia.org/wiki/Laplace_expansion)
* [Wikipedia - Bareiss algorithm](https://en.wikipedia.org/wiki/Bareiss_algorithm)

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

A matrix with symbolic entries is expanded along its rows (a division free Laplace expansion), so
the result contains no denominator that the entries do not have:

```
>> Det({{Sqrt(2),1,0,1},{1,Sqrt(3),1,0},{0,1,Sqrt(5),1},{1,0,1,Sqrt(7)}})
-Sqrt(6)-Sqrt(14)-Sqrt(15)-Sqrt(35)+Sqrt(210)
```

If the entries do have denominators, the result is reported as one cancelled fraction:

```
>> Det({{1/x,1,0,1},{1,1/y,1,0},{0,1,1/z,1},{1,0,1,1/w}})
(1-w*x-x*y-w*z-y*z)/(w*x*y*z)
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Det](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/LinearAlgebra.java#L876) 

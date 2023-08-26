## MatrixQ

```
MatrixQ(m)
```

> returns `True` if `m` is a list of equal-length lists.

```
MatrixQ(m, f)
```

> only returns `True` if `f(x)`  returns `True` for each element `x` of the matrix `m`.

See
* [Wikipedia - Matrix (mathematics)](https://en.wikipedia.org/wiki/Matrix_(mathematics))
* [Youtube - Linear transformations and matrices | Essence of linear algebra, chapter 3](https://youtu.be/kYB8IZa5AuE)
* [Youtube - Matrix multiplication as composition | Essence of linear algebra, chapter 4](https://youtu.be/XkY2DOUCWMU)

### Examples

```
>> MatrixQ({{1, 3}, {4.0, 3/2}}, NumberQ)
True
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of MatrixQ](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/PredicateQ.java#L729) 

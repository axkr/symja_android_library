## VectorQ

```
VectorQ(v)
```

> returns `True` if `v` is a list of elements which are not themselves lists.

```
VectorQ(v, f)
```

> returns `True` if `v` is a vector and `f(x)` returns `True` for each element `x` of `v`.

See
* [Wikipedia - Vector (mathematics and physics)](https://en.wikipedia.org/wiki/Vector_(mathematics_and_physics))
* [Youtube - Vectors, what even are they? | Essence of linear algebra, chapter 1](https://youtu.be/fNk_zzaMoSs)
* [Youtube - Linear combinations, span, and basis vectors | Essence of linear algebra, chapter 2](https://youtu.be/k7RM-ot2NWY)


### Examples

```
>> VectorQ({a, b, c})
True
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of VectorQ](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/PredicateQ.java#L1627) 

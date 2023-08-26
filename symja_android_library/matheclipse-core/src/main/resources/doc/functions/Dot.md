## Dot

```
Dot(x, y) or x . y
```

> `x . y` computes the vector dot product or matrix product `x . y`.

**Note**: the `Dot` operator has the attribute `Flat` ([associative property](https://en.wikipedia.org/wiki/Associative_property)) but not `Orderless` ([commutative property](https://en.wikipedia.org/wiki/Commutative_property)).

See:    
* [Wikipedia - Matrix multiplication](https://en.wikipedia.org/wiki/Matrix_multiplication)
* [Youtube - Matrix multiplication as composition | Essence of linear algebra, chapter 4](https://youtu.be/XkY2DOUCWMU)

### Examples

Scalar product of vectors:

```
>> {a, b, c} . {x, y, z}
a*x+b*y+c*z 
```

Product of matrices and vectors:

```
>> {{a, b}, {c, d}} . {x, y}
{a*x+b*y,c*x+d*y}
```

Matrix product:

```
>> {{a, b}, {c, d}} . {{r, s}, {t, u}}
{{a*r+b*t,a*s+b*u}, {c*r+d*t,c*s+d*u}}

>> a . b
a.b
```

Wrong dimensions print an error message:


```
{{a, b}, {c, d}}.{{x, y}}
```

**Dot: Tensors {{a,b},{c,d}} and {{x,y}} have incompatible shapes.**

### Related terms 
[Flat](Flat.md), [MatrixPower](MatrixPower.md), [Orderless](Orderless.md), [Times](Times.md) 






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Dot](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/LinearAlgebra.java#L1765) 

## KleinInvariantJ

```
KleinInvariantJ(t)
```

> returns the Klein invariant `J(t)` of the complex number `t` in the upper half-plane. It is normalized to `J(I) = 1`, so it is the j-invariant divided by `1728`.

See
* [Wikipedia - j-invariant](https://en.wikipedia.org/wiki/J-invariant)
* [NIST - Klein's complete invariant](https://dlmf.nist.gov/23.15.ii)

### Examples

`J(t)` has the period `1`:

```
>> KleinInvariantJ(I)
1

>> KleinInvariantJ(I+42)
1
```

At some imaginary quadratic arguments the value is an exact integer:

```
>> KleinInvariantJ((1+I*Sqrt(3))/2)
0

>> KleinInvariantJ((1+I*Sqrt(163))/2)
-151931373056000
```

Numeric values with machine or arbitrary precision:

```
>> KleinInvariantJ(0.5+0.1*I)
-3839.63

>> N(KleinInvariantJ(1/3+I), 30)
0.150719494747791082095920221742+I*(-0.121554034224618819751954563386)
```

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of KleinInvariantJ](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/EllipticIntegrals.java#L2884)

* [Rule definitions of KleinInvariantJ](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/rules/KleinInvariantJRules.m)

## Plus

```
Plus(a, b, ...)

a + b + ...
```

> represents the sum of the terms `a, b, ...`. 
 
**Note**: the `Plus` operator has the attribute `Flat` ([associative property](https://en.wikipedia.org/wiki/Associative_property)), `Orderless` ([commutative property](https://en.wikipedia.org/wiki/Commutative_property)), `OneIdentity` and `Listable`.

### Examples

```
>> 1 + 2
3
```

`Plus` performs basic simplification of terms:

```
>> a + b + a
2*a+b

>> a + a + 3 * a
5*a

>> a + b + 4.5 + a + b + a + 2 + 1.5 * b
6.5+3.0*a+3.5*b 
```

Apply `Plus` on a list to sum up its elements:

```
>> Plus @@ {2, 4, 6}
12
```

The sum of the first `1000` integers:

```
>> Plus @@ Range(1000)
500500
```

`Plus` has default value `0`:

```
>> a /. n_. + x_ :> {n, x}
{0,a}

>> -2*a - 2*b
-2*a-2*b
 
>> -4+2*x+2*Sqrt(3)
-4+2*x+2*Sqrt(3)
 
>> 1 - I * Sqrt(3)
1-I*Sqrt(3)
 
>> Head(3 + 2 I)
Complex
 
>> N(Pi, 30) + N(E, 30)
5.85987448204883847382293085463
 
>> N(Pi, 30) + N(E, 30) // Precision
30
```

### Related terms 
[Flat](Flat.md), [Listable](Listable.md), [OneIdentity](OneIdentity.md), [Orderless](Orderless.md) 






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Plus](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Arithmetic.java#L2735) 

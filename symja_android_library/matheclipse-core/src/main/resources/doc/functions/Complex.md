## Complex

```
Complex
```

> is the head of complex numbers.

```
Complex(a, b)
```

> constructs the complex number `a + I * b`.

See 
* [Wikipedia - Complex number](https://en.wikipedia.org/wiki/Complex_number) 

### Examples

```
>> Head(2 + 3*I)
Complex

>> Complex(1, 2/3)
1+I*2/3

>> Abs(Complex(3, 4))
5

>> -2 / 3 - I
-2/3-I

>> Complex(10, 0)
10

>> 0. + I
I*1.0

>> 1 + 0*I
1

>> Head(1 + 0*I)
Integer

>> Complex(0.0, 0.0)
0.0

>> 0.*I
0.0

>> 0. + 0.*I
0.0

>> 1. + 0.*I
1.0

>> 0. + 1.*I
I*1.0
```

Check nesting Complex

```
>> Complex(1, Complex(0, 1))
0

>> Complex(1, Complex(1, 0))
1+I 

>> Complex(1, Complex(1, 1))
I
```

### Related terms 
[I](I.md), [Im](Im.md), [Re](Re.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Complex](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Arithmetic.java#L821) 

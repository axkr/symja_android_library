## Power

```
Power(a, b)  

a ^ b
```

> represents `a` raised to the power of `b`.
	
	
**Note:** `Power` has the `Listable` attribute and `Power(x,y,z)` is grouped as `x^(y^z)`

You can't do matrix calculations with the `^operator`:

```
>> {{2,1},{1,1}} ^ 2
```

is computed as:

```
{{2^2,1^2},{1^2,1^2}}
```

Use `Inverse({{2,1},{1,1}})` or `MatrixPower({{2,1},{1,1}},2)` to calculate matrix inverses and powers.

Don't confuse the `^` operator with the `^^` operator, which can be used for integer number bases other than `10`. 
Here's an example for a hexadecimal number:

```
>> 16^^abcdefff
2882400255
```

See
* [Wikipedia - Exponentiation](https://en.wikipedia.org/wiki/Exponentiation)
* [Fungrim - Powers](http://fungrim.org/topic/Powers/)

### Examples
 
```
>> 4 ^ (1/2)
2
 
>> 4 ^ (1/3)
4^(1/3)
 
>> 3^123
48519278097689642681155855396759336072749841943521979872827
 
>> (y ^ 2) ^ (1/2)
Sqrt(y^2)
 
>> (y ^ 2) ^ 3
y^6
```

Use a decimal point to force numeric evaluation:

```
>> 4.0 ^ (1/3)
1.5874010519681994
```

`Power` has default value `1` for its second argument:

```
>> a /. x_ ^ n_. :> {x, n}
{a,1}
```

`Power` can be used with complex numbers:

```
>> (1.5 + 1.0*I) ^ 3.5
-3.682940057821917+I*6.951392664028508
 
>> (1.5 + 1.0*I) ^ (3.5 + 1.5*I)
-3.1918162904562815+I*0.6456585094161581
```

Infinite expression 0^(negative number)

```
>> 1/0 
ComplexInfinity

>> 0 ^ -2
ComplexInfinity

>> 0 ^ (-1/2)
ComplexInfinity

>> 0 ^ -Pi
ComplexInfinity
```

Indeterminate expression 0 ^ (complex number) encountered.

```
>> 0 ^ (2*I*E)
Indeterminate
 
>> 0 ^ - (Pi + 2*E*I)
ComplexInfinity
```

Indeterminate expression 0 ^ 0 encountered.

```
>> 0 ^ 0
Indeterminate

>> Sqrt(-3+2.*I)
0.5502505227003375+I*1.8173540210239707
 
>> Sqrt(-3+2*I)
Sqrt(-3+I*2) 
 
>> (3/2+1/2I)^2
2+I*3/2
 
>> I ^ I
I^I
 
>> 2 ^ 2.0
4.0
 
>> Pi ^ 4.
97.40909103400242
 
>> a ^ b
a^b

>> Power(x,y,z)
Power(x,Power(y,z))
```
 
### Related terms

[BaseForm](BaseForm.md), [MatrixPower](MatrixPower.md), [Times](Times.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Power](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Arithmetic.java#L3342) 

* [Rule definitions of Power](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/rules/PowerRules.m) 

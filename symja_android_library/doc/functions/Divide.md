## Divide

```
Divide(a, b)

a / b
```

> represents the division of  `a` by `b`. 
 

### Examples

```
>> 30 / 5
6

>> 1 / 8
1/8

>> Pi / 4
Pi / 4
```

Use `N` or a decimal point to force numeric evaluation:

```
>> Pi / 4.0
0.7853981633974483
 
>> N(1 / 8)
0.125
```

Nested divisions:

```
>> a / b / c
a/(b*c)
 
>> a / (b / c)
(a*c)/b
 
>> a / b / (c / (d / e))
(a*d)/(b*c*e)
 
>> a / (b ^ 2 * c ^ 3 / e)
(a*e)/(b^2*c^3) 
 
>> 1 / 4.0
0.25
 
>> 10 / 3 // FullForm
Rational(10,3)
 
>> a / b // FullForm
Times(a, Power(b, -1))
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Divide](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Arithmetic.java#L1344) 

## Sqrt

```
Sqrt(expr)
```

> returns the square root of `expr`.
 
See
* [Wikipedia - Square root](https://en.wikipedia.org/wiki/Square_root)
* [Fungrim - Square roots](http://fungrim.org/topic/Square_roots/)

### Examples

```
>> Sqrt(4)
2

>> Sqrt(5)
Sqrt(5)

>> Sqrt(5) // N
2.23606797749979

>> Sqrt(a)^2
a
```

Complex numbers:

```
>> Sqrt(-4)
I*2

>> I == Sqrt(-1)
True
 
>> N(Sqrt(2), 50)
1.41421356237309504880168872420969807856967187537694 
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Sqrt](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Arithmetic.java#L5294) 

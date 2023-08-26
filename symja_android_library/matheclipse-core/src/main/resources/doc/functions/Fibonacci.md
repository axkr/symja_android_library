## Fibonacci

```
Fibonacci(n)
```

> returns the Fibonacci number of the integer `n` 

```
Fibonacci(n, var)
```

> returns the `n`th Fibonacci polynomial for variable `var`.

See 
* [Wikipedia - Fibonacci number](https://en.wikipedia.org/wiki/Fibonacci_number)
* [Wikipedia - Fibonacci polynomials](https://en.wikipedia.org/wiki/Fibonacci_polynomials)
* [Fungrim - Fibonacci numbers](http://fungrim.org/topic/Fibonacci_numbers/)

### Examples

```
>> Fibonacci(0)
0
 
>> Fibonacci(1)
1
 
>> Fibonacci(10)
55
 
>> Fibonacci(-52) 
-32951280099

>> Fibonacci(200)
280571172992510140037611932413038677189525

>> Fibonacci(50, x) 
25*x+2600*x^3+80730*x^5+1184040*x^7+10015005*x^9+54627300*x^11+206253075*x^13+  
565722720*x^15+1166803110*x^17+1855967520*x^19+2319959400*x^21+2310789600*x^23+ 
1852482996*x^25+1203322288*x^27+635745396*x^29+273438880*x^31+95548245*x^33+ 
26978328*x^35+6096454*x^37+1086008*x^39+148995*x^41+15180*x^43+1081*x^45+48*x^47+x^49
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Fibonacci](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/NumberTheory.java#L2568) 

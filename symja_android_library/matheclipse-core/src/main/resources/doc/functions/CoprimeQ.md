## CoprimeQ
```
CoprimeQ(x, y)
```

> tests whether `x` and `y` are coprime by computing their greatest common divisor.

See:
* [Wikipedia - Coprime](http://en.wikipedia.org/wiki/Coprime)

### Examples
```
>> CoprimeQ(7, 9)
True
>> CoprimeQ(-4, 9)
True
>> CoprimeQ(12, 15)
False 
>> CoprimeQ(2, 3, 5)
True
>> CoprimeQ(2, 4, 5)
False
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of CoprimeQ](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/NumberTheory.java#L1250) 

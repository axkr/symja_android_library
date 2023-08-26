## Binomial

```
Binomial(n, k)
```

> returns the binomial coefficient of the 2 integers `n` and `k`

For negative integers `k` this function will return `0` no matter what value is the other argument `n`.

See:  
* [Wikipedia - Binomial coefficient](http://en.wikipedia.org/wiki/Binomial_coefficient)
* [John D. Cook - Binomial coefficients definition](https://www.johndcook.com/blog/binomial_coefficients/)
* [Kronenburg 2011 - The Binomial Coefficient for Negative Arguments](https://arxiv.org/pdf/1105.3689.pdf)
* [Fungrim - Factorials and binomial coefficients](http://fungrim.org/topic/Factorials_and_binomial_coefficients/)

### Examples

``` 
>> Binomial(4,2)
6
 
>> Binomial(5, 3)   
10   
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Binomial](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/NumberTheory.java#L360) 

## Multinomial

```
Multinomial(n1, n2, ...)
```

> gives the multinomial coefficient `(n1+n2+...)!/(n1! n2! ...)`.

See
* [Wikipedia: Multinomial coefficient](http://en.wikipedia.org/wiki/Multinomial_coefficient)

### Examples

```
>> Multinomial(2, 3, 4, 5)
2522520

>> Multinomial()
1
```
 
`Multinomial(n-k, k)` is equivalent to `Binomial(n, k)`.

```
>> Multinomial(2, 3)
10
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Multinomial](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/NumberTheory.java#L3611) 

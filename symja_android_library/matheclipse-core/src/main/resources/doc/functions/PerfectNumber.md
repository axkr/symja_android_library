## PerfectNumber

```
PerfectNumber(n)
```

> returns the `n`th perfect number. In number theory, a perfect number is a positive integer that is equal to the sum of its proper 
positive divisors, that is, the sum of its positive divisors excluding the number itself. Currently `0 <= n <= 47` can be computed, otherwise the function returns unevaluated.

See
* [Wikipedia - Perfect number](https://en.wikipedia.org/wiki/Perfect_number)
* [Wikipedia - List of perfect numbers](https://en.wikipedia.org/wiki/List_of_perfect_numbers)

### Examples

```
>> Table(PerfectNumber(i), {i,5})
{6,28,496,8128,33550336}
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of PerfectNumber](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/NumberTheory.java#L4153) 

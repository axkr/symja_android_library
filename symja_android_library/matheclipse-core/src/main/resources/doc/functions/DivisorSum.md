## DivisorSum

```
DivisorSum(n, head)
```

>  returns the sum of the divisors of `n`. The `head` is applied to each divisor.

```
DivisorSum(n, head, condition)
```

>  The `condition` checks, if the divisor should be summed up.


See:  
* [Wikipedia - Divisor sum identities](https://en.wikipedia.org/wiki/Divisor_sum_identities)

## Examples

Calculate the [OEIS - sequence A002791](https://oeis.org/A002791):

```
>> a(n_) := DivisorSum(n, #^2 &, # < 5 &) + 4 * DivisorSum(n, # &, # > 4 &); Array(a, 70)
{1,5,10,21,21,38,29,53,46,65,45,102,53,89,90,117,69,146,77,161,122,137,93,230,
121,161,154,217,117,278,125,245,186,209,189,354,149,233,218,353,165,374,173,329,
306,281,189,486,225,365,282,385,213,470,285,473,314,353,237,662,245,377,410,501,
333,566,269,497,378,569}
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of DivisorSum](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/NumberTheory.java#L1489) 

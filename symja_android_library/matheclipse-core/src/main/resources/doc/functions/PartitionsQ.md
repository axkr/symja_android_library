## PartitionsQ

```
PartitionsQ(n)
```
> gives the number of partitions of the integer `n` into distinct parts

`PartitionsP` and `PartitionsQ` use Euler's pentagonal number recurrences; `n` may be up to `200000`.

See
* [Wikipedia - Partition (number theory)](https://en.wikipedia.org/wiki/Partition_(number_theory))

### Examples

```
>> PartitionsQ(50)
3658

>> PartitionsQ(6)
4

```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of PartitionsQ](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/NumberTheory.java#L4681) 

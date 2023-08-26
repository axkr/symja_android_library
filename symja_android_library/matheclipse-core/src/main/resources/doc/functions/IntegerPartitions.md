## IntegerPartitions

```
IntegerPartitions(n)
```

> returns all partitions of the integer `n`.
 
```
IntegerPartitions(n, k)
```

> lists the possible ways to partition `n` into smaller integers, using up to `k` elements.

```
IntegerPartitions(n, {lower, upper}, {list-of-integers})
```

> lists the possible ways to partition `n` with the numbers in `{list-of-integers}`, using between `lower` and `upper` number of elements.

See
* [Wikipedia - Partition (number theory)](https://en.wikipedia.org/wiki/Partition_(number_theory))
* [Wikipedia - Coin problem](https://en.wikipedia.org/wiki/Coin_problem)
* [Wikipedia - Diophantine equation](https://en.wikipedia.org/wiki/Diophantine_equation)

### Examples

```
>> IntegerPartitions(3)
{{3},{2,1},{1,1,1}}

>> IntegerPartitions(10,2)
{{10},{9,1},{8,2},{7,3},{6,4},{5,5}}
```

The "McNugget partitions" [OEIS - Number of partitions of n into parts 6, 9 or 20](https://oeis.org/A214772).

```
>> Table(Length(IntegerPartitions(i, All, {6, 9, 20})), {i,0, 100, 1}) 
{1,0,0,0,0,0,1,0,0,1,0,0,1,0,0,1,0,0,2,0,1,1,0,0,2,0,1,2,0,1,2,0,1,2,0,1,3,0,2,2, 
1,1,3,0,2,3,1,2,3,1,2,3,1,2,4,1,3,3,2,2,5,1,3,4,2,3,5,2,3,5,2,3,6,2,4,5,3,3,7,2, 
5,6,3,4,7,3,5,7,3,5,8,3,6,7,4,5,9,3,7,8,5}
```

### Related terms 
[FrobeniusNumber](FrobeniusNumber.md), [FrobeniusSolve](FrobeniusSolve.md) 






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of IntegerPartitions](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Combinatoric.java#L686) 

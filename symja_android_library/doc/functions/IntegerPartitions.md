## IntegerPartitions

```
IntegerPartitions(n)
```

> returns all partitions of the integer `n`.
 
```
IntegerPartitions(n, k)
```

> lists the possible ways to partition `n` into smaller integers, using up to `k` elements.

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

### Related terms 
[FrobeniusNumber](FrobeniusNumber.md), [FrobeniusSolve](FrobeniusSolve.md) 
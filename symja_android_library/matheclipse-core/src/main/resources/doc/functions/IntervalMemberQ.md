## IntervalMemberQ
 
```
IntervalMemberQ(interval, interval-or-real-number)
```

> returns `True`, if `interval-or-real-number` is completly sourrounded by `interval`


See 
* [Wikipedia - Interval arithmetic](https://en.wikipedia.org/wiki/Interval_arithmetic)
* [Wikipedia - Interval (mathematics)](https://en.wikipedia.org/wiki/Interval_(mathematics))

### Examples

```
>> IntervalMemberQ(Interval({4,10}), Interval({2*Pi, 3*Pi})) 
True

>> IntervalMemberQ(Interval({4,10}), Interval({2*Pi, 4*Pi})) 
False
```

### Related terms 
[Interval](Interval.md), [IntervalIntersection](IntervalIntersection.md), [IntervalUnion](IntervalUnion.md) 
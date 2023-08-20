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






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of IntervalMemberQ](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/IntervalFunctions.java#L347) 

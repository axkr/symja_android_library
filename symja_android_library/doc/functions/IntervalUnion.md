## IntervalUnion
 
```
IntervalUnion(interval_1, interval_2, ...)
```

> compute the union of the intervals `interval_1, interval_2, ...`


See:
* [Wikipedia - Interval arithmetic](https://en.wikipedia.org/wiki/Interval_arithmetic)
* [Wikipedia - Interval (mathematics)](https://en.wikipedia.org/wiki/Interval_(mathematics))

### Examples

```
>> IntervalUnion(Interval({1, 2}, {3, 4}, {5, 7}, {8, 8.5}), Interval({1.5, 3.5}, {4.1, 6}, {9, 10}))
Interval({1,4},{4.1,7},{8,8.5},{9,10})
```

### Related terms 
[Interval](Interval.md), [IntervalIntersection](IntervalIntersection.md), [IntervalMemberQ](IntervalMemberQ.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of IntervalUnion](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/IntervalFunctions.java#L664) 

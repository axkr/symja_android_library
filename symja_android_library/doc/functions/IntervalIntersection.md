## IntervalIntersection
 
```
IntervalIntersection(interval_1, interval_2, ...)
```

> compute the intersection of the intervals `interval_1, interval_2, ...`


See:
* [Wikipedia - Interval arithmetic](https://en.wikipedia.org/wiki/Interval_arithmetic)
* [Wikipedia - Interval (mathematics)](https://en.wikipedia.org/wiki/Interval_(mathematics))

### Examples

```
>> IntervalIntersection(Interval({1, 2}, {3, 4}, {5, 7}, {8, 8.5}), Interval({1.5, 3.5}, {4.1, 6}, {9, 10})) 
Interval({1.5,2},{3,3.5},{5,6})
```

### Related terms 
[Interval](Interval.md), [IntervalMemberQ](IntervalMemberQ.md), [IntervalUnion](IntervalUnion.md) 






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of IntervalIntersection](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/IntervalFunctions.java#L514) 

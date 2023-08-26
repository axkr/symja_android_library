## IntervalData

```
IntervalData({a, left-end, right-end, b})
```

> represents the open/closed ends interval from `a` to `b`. `left-end` and `right-end` must have the value `Less` for representing an open ended interval or `LessEqual` for representing a closed ended interval.


See 
* [Wikipedia - Interval arithmetic](https://en.wikipedia.org/wiki/Interval_arithmetic)
* [Wikipedia - Interval (mathematics)](https://en.wikipedia.org/wiki/Interval_(mathematics))

### Examples

```
>> IntervalData({1,Less,Less, 6}) * IntervalData({0, Less,Less,2}) 
IntervalData({0,Less,Less,12})
```

### Related terms 
[IntervalComplement](IntervalComplement.md), [IntervalIntersection](IntervalIntersection.md), [IntervalMemberQ](IntervalMemberQ.md), [IntervalUnion](IntervalUnion.md) 
 






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of IntervalData](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/IntervalFunctions.java#L262) 

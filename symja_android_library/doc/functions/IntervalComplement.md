## IntervalComplement
 
```
IntervalComplement(interval_1, interval_2)
```

> compute the complement of the intervals `interval_1 \ interval_2`. The intervals must be of structure `IntervalData` (closed/opened ends of interval) and not of structure `Interval` (only closed ends)


See:
* [Wikipedia - Complement (set theory) Relative complement](https://en.wikipedia.org/wiki/Complement_(set_theory)#Relative_complement)
* [Wikipedia - Interval (mathematics)](https://en.wikipedia.org/wiki/Interval_(mathematics))

### Examples

```
>> IntervalComplement(IntervalData({1/2, LessEqual, LessEqual, 3}), IntervalData({0, LessEqual, LessEqual, 1}, {E, LessEqual, Less, 4})) 
IntervalData({1,Less,Less,E})
```

### Related terms 
[IntervalData](IntervalData.md), [IntervalIntersection](IntervalIntersection.md), [IntervalMemberQ](IntervalMemberQ.md)
 






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of IntervalComplement](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/IntervalFunctions.java#L105) 

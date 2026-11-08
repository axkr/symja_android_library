## FromAbsoluteTime

```
FromAbsoluteTime(t)
```

> returns a `DateObject` for the absolute time `t`, the number of seconds since the beginning of January 1, 1900.

### Examples

```
>> FromAbsoluteTime(0)
DateObject({1900,1,1,0,0,0},Instant,Gregorian,0.0)

>> FromAbsoluteTime(3155673600)
DateObject({2000,1,1,0,0,0},Instant,Gregorian,0.0)
```

`FromAbsoluteTime` is the inverse of `AbsoluteTime`:

```
>> FromAbsoluteTime(AbsoluteTime({2024, 6, 14, 10, 30, 0}))
DateObject({2024,6,14,10,30,0},Instant,Gregorian,0.0)
```

### Related terms
[AbsoluteTime](AbsoluteTime.md), [DateList](DateList.md), [DateObject](DateObject.md), [FromJulianDate](FromJulianDate.md), [FromUnixTime](FromUnixTime.md), [JulianDate](JulianDate.md), [UnixTime](UnixTime.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of FromAbsoluteTime](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/DateTimeFunctions.java)

## FromUnixTime

```
FromUnixTime(t)
```

> returns a `DateObject` for the Unix time `t`, the number of seconds since the beginning of January 1, 1970, in UTC.

```
FromUnixTime(t, TimeZone -> z)
```

> returns the date in the time zone `z`, given as an offset in hours from UTC.

See
* [Wikipedia - Unix time](https://en.wikipedia.org/wiki/Unix_time)

### Examples

```
>> FromUnixTime(0)
DateObject({1970,1,1,0,0,0},Instant,Gregorian,0.0)

>> FromUnixTime(1000000000)
DateObject({2001,9,9,1,46,40},Instant,Gregorian,0.0)
```

The same instant in other time zones:

```
>> FromUnixTime(0, TimeZone -> -5)
DateObject({1969,12,31,19,0,0},Instant,Gregorian,-5.0)

>> FromUnixTime(0, TimeZone -> 5.5)
DateObject({1970,1,1,5,30,0},Instant,Gregorian,5.5)
```

### Related terms
[AbsoluteTime](AbsoluteTime.md), [DateList](DateList.md), [DateObject](DateObject.md), [FromAbsoluteTime](FromAbsoluteTime.md), [FromJulianDate](FromJulianDate.md), [JulianDate](JulianDate.md), [UnixTime](UnixTime.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of FromUnixTime](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/DateTimeFunctions.java)

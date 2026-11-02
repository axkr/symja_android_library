## UnixTime

```
UnixTime()
```

> returns the current Unix time, the number of seconds since the beginning of January 1, 1970, in UTC.

```
UnixTime(date)
```

> returns the Unix time of `date`.

A date can be given as a date list `{y, m, d, h, min, s}` (trailing elements may be left out), a `DateObject`, a date string or a number of seconds since the beginning of January 1, 1900. The result is always an integer; a fraction of a second is dropped.

See
* [Wikipedia - Unix time](https://en.wikipedia.org/wiki/Unix_time)

### Examples

```
>> UnixTime({1970, 1, 1})
0

>> UnixTime({2020, 1, 1, 0, 0, 0})
1577836800

>> UnixTime(DateObject({2001, 9, 9, 1, 46, 40}))
1000000000
```

A date in another time zone gives back the same Unix time:

```
>> UnixTime(FromUnixTime(1000000000, TimeZone -> 5.5))
1000000000
```

### Related terms
[AbsoluteTime](AbsoluteTime.md), [DateList](DateList.md), [DateObject](DateObject.md), [FromAbsoluteTime](FromAbsoluteTime.md), [FromJulianDate](FromJulianDate.md), [FromUnixTime](FromUnixTime.md), [JulianDate](JulianDate.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of UnixTime](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/DateTimeFunctions.java)

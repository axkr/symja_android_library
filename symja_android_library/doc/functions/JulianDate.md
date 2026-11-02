## JulianDate

```
JulianDate()
```

> returns the current Julian date.

```
JulianDate(date)
```

> returns the Julian date of `date`: the number of days since noon UTC on January 1, 4713 BC in the proleptic Julian calendar.

A date can be given as a date list `{y, m, d, h, min, s}` (trailing elements may be left out), a `DateObject`, a date string or a number of seconds since the beginning of January 1, 1900.

See
* [Wikipedia - Julian day](https://en.wikipedia.org/wiki/Julian_day)

### Examples

The Julian date of the J2000 epoch:

```
>> JulianDate({2000, 1, 1, 12, 0, 0})
2.45155*10^6

>> InputForm(JulianDate({2000, 1, 1, 12, 0, 0}))
2451545.0`
```

```
>> JulianDate({2023, 2, 24, 12}) == 2460000
True
```

### Related terms
[AbsoluteTime](AbsoluteTime.md), [DateList](DateList.md), [DateObject](DateObject.md), [FromAbsoluteTime](FromAbsoluteTime.md), [FromJulianDate](FromJulianDate.md), [FromUnixTime](FromUnixTime.md), [UnixTime](UnixTime.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of JulianDate](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/DateTimeFunctions.java)

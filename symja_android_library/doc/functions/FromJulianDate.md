## FromJulianDate

```
FromJulianDate(jd)
```

> returns a `DateObject` for the Julian date `jd`.

See
* [Wikipedia - Julian day](https://en.wikipedia.org/wiki/Julian_day)

### Examples

A Julian day begins at noon:

```
>> FromJulianDate(2460000)
DateObject({2023,2,24,12,0,0},Instant,Gregorian,0.0)

>> FromJulianDate(2460000 + 1/4)
DateObject({2023,2,24,18,0,0},Instant,Gregorian,0.0)

>> FromJulianDate(2460000.5)
DateObject({2023,2,25,0,0,0.0},Instant,Gregorian,0.0)
```

The Julian date `0`, given in the proleptic Gregorian calendar:

```
>> FromJulianDate(0)
DateObject({-4714,11,24,12,0,0},Instant,Gregorian,0.0)
```

### Related terms
[AbsoluteTime](AbsoluteTime.md), [DateList](DateList.md), [DateObject](DateObject.md), [FromAbsoluteTime](FromAbsoluteTime.md), [FromUnixTime](FromUnixTime.md), [JulianDate](JulianDate.md), [UnixTime](UnixTime.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of FromJulianDate](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/DateTimeFunctions.java)

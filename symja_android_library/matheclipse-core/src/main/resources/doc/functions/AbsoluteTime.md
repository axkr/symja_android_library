## AbsoluteTime

```
AbsoluteTime()
```

> returns the number of seconds since the beginning of January 1, 1900, as a real number.

```
AbsoluteTime(date)
```

> returns the number of seconds from the beginning of January 1, 1900, to `date`.

A date can be given as a date list `{y, m, d, h, min, s}` (trailing elements may be left out), a `DateObject`, a date string or a number of seconds since the beginning of January 1, 1900. A date string can be given together with its format as `{"string", {e1, e2, ...}}`.

### Examples

```
>> AbsoluteTime({2000, 1, 1})
3155673600

>> AbsoluteTime({1991, 6, 6, 12, 0, 0})
2885198400

>> AbsoluteTime(DateObject({2020, 3, 5, 6, 7, 8}))
3792377228
```

A date string, and a date string with its format:

```
>> AbsoluteTime("6 June 1991")
2885155200

>> AbsoluteTime({"6-6-91", {"Day", "Month", "YearShort"}})
2.88516*10^9
```

A month beyond `12` carries over into the following years:

```
>> DateList(AbsoluteTime({2020, 25, 1}))
{2022,1,1,0,0,0.0}
```

### Related terms
[DateList](DateList.md), [DateObject](DateObject.md), [DateString](DateString.md), [FromAbsoluteTime](FromAbsoluteTime.md), [FromJulianDate](FromJulianDate.md), [FromUnixTime](FromUnixTime.md), [JulianDate](JulianDate.md), [UnixTime](UnixTime.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of AbsoluteTime](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/DateTimeFunctions.java)

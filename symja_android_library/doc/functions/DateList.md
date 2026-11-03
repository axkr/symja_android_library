## DateList

```
DateList()
```

> returns the current local date as a list `{y, m, d, h, min, s}`.

```
DateList(date)
```

> returns `date` as a list `{y, m, d, h, min, s}`.

```
DateList(date, TimeZone -> z)
```

> returns `date` as the local time of the time zone `z`, given as an offset in hours from UTC.

A date can be given as a date list `{y, m, d, h, min, s}` (trailing elements may be left out), a `DateObject`, a date string or a number of seconds since the beginning of January 1, 1900. A date string can be given together with its format as `{"string", {e1, e2, ...}}`. Values outside of their normal range carry over into the next larger unit.

### Examples

```
>> DateList(0)
{1900,1,1,0,0,0.0}

>> DateList(3155673600)
{2000,1,1,0,0,0.0}

>> DateList(DateObject({2026, 3, 15, 10, 30, 45}))
{2026,3,15,10,30,45.0}
```

The day `300` of the year 2012:

```
>> DateList({2012, 1, 300, 10})
{2012,10,26,10,0,0.0}

>> DateList({2024, 2, 30})
{2024,3,1,0,0,0.0}
```

Date strings:

```
>> DateList("2022-11-23 14:30:00")
{2022,11,23,14,30,0.0}

>> DateList({"31/10/91", {"Day", "Month", "YearShort"}})
{1991,10,31,0,0,0.0}
```

Noon in UTC is half past five in the afternoon in the time zone `5.5`:

```
>> DateList(DateObject({2024, 1, 1, 12, 0, 0}, "Instant", "Gregorian", 0), TimeZone -> 5.5)
{2024,1,1,17,30,0.0}
```

### Related terms
[AbsoluteTime](AbsoluteTime.md), [DateObject](DateObject.md), [DatePlus](DatePlus.md), [DateString](DateString.md), [FromAbsoluteTime](FromAbsoluteTime.md), [FromJulianDate](FromJulianDate.md), [FromUnixTime](FromUnixTime.md), [JulianDate](JulianDate.md), [UnixTime](UnixTime.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of DateList](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/DateTimeFunctions.java)

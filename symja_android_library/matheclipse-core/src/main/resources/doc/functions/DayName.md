## DayName

```
DayName(date)
```

> returns the day of the week of `date` as one of the symbols `Monday`, `Tuesday`, ..., `Sunday`.

A date can be given as a date list `{y, m, d, h, min, s}` (trailing elements may be left out), a `DateObject`, a date string or a number of seconds since the beginning of January 1, 1900. A date given as a single integer is a number of seconds, not a year.

### Examples

```
>> DayName({2000, 1, 1})
Saturday

>> DayName({1970, 1, 1})
Thursday

>> DayName(DateObject({2024, 6, 15}))
Saturday
```

January 1, 1900 was a Monday:

```
>> DayName(0)
Monday
```

### Related terms
[DateObject](DateObject.md), [DayMatchQ](DayMatchQ.md), [DayRound](DayRound.md), [NextDate](NextDate.md), [PreviousDate](PreviousDate.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of DayName](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/DateTimeFunctions.java)

## DayRound

```
DayRound(date)
```

> rounds `date` down to the beginning of its day.

```
DayRound(date, dayspec)
```

> returns the first day on or after `date` which matches the day specification `dayspec`.

A day specification is one of the symbols `Monday`, `Tuesday`, ..., `Sunday`, or one of the strings `"Weekday"` or `"BusinessDay"` (Monday to Friday), `"Weekend"` or `"WeekendDay"` (Saturday and Sunday), `"BeginningOfMonth"`, `"EndOfMonth"` and `"All"`. Holidays are not taken into account.

### Examples

```
>> DayRound({2024, 3, 15, 23, 59, 0})
DateObject({2024,3,15},Day)
```

June 15, 2024 is a Saturday:

```
>> DayRound({2024, 6, 15}, Monday)
DateObject({2024,6,17},Day)

>> DayRound({2024, 6, 15}, "BusinessDay")
DateObject({2024,6,17},Day)
```

A day which already matches is kept:

```
>> DayRound({2024, 6, 17}, Monday)
DateObject({2024,6,17},Day)
```

### Related terms
[DayMatchQ](DayMatchQ.md), [DayName](DayName.md), [DayPlus](DayPlus.md), [NextDate](NextDate.md), [PreviousDate](PreviousDate.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of DayRound](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/DateTimeFunctions.java)

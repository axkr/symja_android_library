## DayCount

```
DayCount(date1, date2)
```

> returns the number of days from `date1` to `date2`.

```
DayCount(date1, date2, dayspec)
```

> returns the number of days after `date1` up to and including `date2` which match the day specification `dayspec`.

A day specification is one of the symbols `Monday`, `Tuesday`, ..., `Sunday`, or one of the strings `"Weekday"` or `"BusinessDay"` (Monday to Friday), `"Weekend"` or `"WeekendDay"` (Saturday and Sunday), `"BeginningOfMonth"`, `"EndOfMonth"` and `"All"`. Holidays are not taken into account.

### Examples

```
>> DayCount({2024, 1, 1}, {2024, 2, 1})
31

>> DayCount({2024, 2, 1}, {2024, 1, 1})
-31
```

The Mondays and the weekend days after January 1, 2024, which is a Monday, up to February 1, 2024:

```
>> DayCount({2024, 1, 1}, {2024, 2, 1}, Monday)
4

>> DayCount({2024, 1, 1}, {2024, 2, 1}, "Weekend")
8
```

```
>> DayCount({2024, 1, 1}, {2024, 12, 31}, "EndOfMonth")
12
```

### Related terms
[DateDifference](DateDifference.md), [DayMatchQ](DayMatchQ.md), [DayPlus](DayPlus.md), [DayRange](DayRange.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of DayCount](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/DateTimeFunctions.java)

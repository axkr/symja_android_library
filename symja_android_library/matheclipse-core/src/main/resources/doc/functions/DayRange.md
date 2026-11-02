## DayRange

```
DayRange(date1, date2)
```

> returns the list of all days from `date1` to `date2`.

```
DayRange(date1, date2, dayspec)
```

> returns the days from `date1` to `date2` which match the day specification `dayspec`.

A day specification is one of the symbols `Monday`, `Tuesday`, ..., `Sunday`, or one of the strings `"Weekday"` or `"BusinessDay"` (Monday to Friday), `"Weekend"` or `"WeekendDay"` (Saturday and Sunday), `"BeginningOfMonth"`, `"EndOfMonth"` and `"All"`. Holidays are not taken into account. The days are given as `DateObject`s in increasing order.

### Examples

```
>> DayRange({2020, 2, 28}, {2020, 3, 1})
{DateObject({2020,2,28},Day),DateObject({2020,2,29},Day),DateObject({2020,3,1},Day)}
```

The weekend days in the first ten days of June 2024:

```
>> Length(DayRange({2024, 6, 1}, {2024, 6, 10}, "Weekend"))
4
```

The Sundays of January 2013:

```
>> Map(DateList, DayRange({2013, 1}, {2013, 1, 31}, Sunday))
{{2013,1,6,0,0,0.0},{2013,1,13,0,0,0.0},{2013,1,20,0,0,0.0},{2013,1,27,0,0,0.0}}
```

### Related terms
[DateRange](DateRange.md), [DayCount](DayCount.md), [DayMatchQ](DayMatchQ.md), [DayPlus](DayPlus.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of DayRange](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/DateTimeFunctions.java)

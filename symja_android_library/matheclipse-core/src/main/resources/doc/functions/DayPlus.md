## DayPlus

```
DayPlus(date, n)
```

> returns the date `n` days after `date`.

```
DayPlus(date, n, dayspec)
```

> returns the `n`-th day after `date` which matches the day specification `dayspec`.

A day specification is one of the symbols `Monday`, `Tuesday`, ..., `Sunday`, or one of the strings `"Weekday"` or `"BusinessDay"` (Monday to Friday), `"Weekend"` or `"WeekendDay"` (Saturday and Sunday), `"BeginningOfMonth"`, `"EndOfMonth"` and `"All"`. Holidays are not taken into account. A negative `n` counts backwards.

### Examples

```
>> DayPlus({2024, 2, 28}, 1)
DateObject({2024,2,29},Day)

>> DayPlus({2024, 1, 15}, -5)
DateObject({2024,1,10},Day)
```

Ten business days after Monday, January 15, 2024:

```
>> DayPlus({2024, 1, 15}, 10, "BusinessDay")
DateObject({2024,1,29},Day)
```

The second Sunday after January 15, 2024:

```
>> DayPlus({2024, 1, 15}, 2, Sunday)
DateObject({2024,1,28},Day)
```

### Related terms
[DatePlus](DatePlus.md), [DayCount](DayCount.md), [DayMatchQ](DayMatchQ.md), [DayRange](DayRange.md), [NextDate](NextDate.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of DayPlus](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/DateTimeFunctions.java)

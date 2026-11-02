## DayMatchQ

```
DayMatchQ(date, dayspec)
```

> returns `True` if `date` matches the day specification `dayspec`, and `False` otherwise.

A day specification is one of the symbols `Monday`, `Tuesday`, ..., `Sunday`, or one of the strings `"Weekday"` or `"BusinessDay"` (Monday to Friday), `"Weekend"` or `"WeekendDay"` (Saturday and Sunday), `"BeginningOfMonth"`, `"EndOfMonth"` and `"All"`. Holidays are not taken into account.

### Examples

June 15, 2024 is a Saturday:

```
>> DayMatchQ({2024, 6, 15}, Saturday)
True

>> DayMatchQ({2024, 6, 15}, "Weekend")
True

>> DayMatchQ({2024, 6, 15}, "BusinessDay")
False
```

```
>> DayMatchQ({2024, 1, 31}, "EndOfMonth")
True
```

### Related terms
[DayCount](DayCount.md), [DayName](DayName.md), [DayPlus](DayPlus.md), [DayRange](DayRange.md), [DayRound](DayRound.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of DayMatchQ](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/DateTimeFunctions.java)

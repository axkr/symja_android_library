## PreviousDate

```
PreviousDate(date, dayspec)
```

> returns the last day before `date` which matches the day specification `dayspec`.

```
PreviousDate(date, "unit")
```

> returns the previous calendar unit, such as `"Day"`, `"Month"` or `"Year"`, before the one containing `date`.

```
PreviousDate(spec)
```

> uses the current date.

A day specification is one of the symbols `Monday`, `Tuesday`, ..., `Sunday`.

### Examples

June 22, 2024 is a Saturday:

```
>> PreviousDate({2024, 6, 22}, Sunday)
DateObject({2024,6,16},Day)
```

```
>> PreviousDate({2024, 3, 1}, Thursday)
DateObject({2024,2,29},Day)

>> PreviousDate(DateObject({2024, 3, 1}), "Day")
DateObject({2024,2,29},Day)

>> PreviousDate(DateObject({2024, 1, 1}), "Month")
DateObject({2023,12},Month)
```

### Related terms
[DatePlus](DatePlus.md), [DayPlus](DayPlus.md), [DayRound](DayRound.md), [NextDate](NextDate.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of PreviousDate](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/DateTimeFunctions.java)

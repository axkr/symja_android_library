## NextDate

```
NextDate(date, dayspec)
```

> returns the first day after `date` which matches the day specification `dayspec`.

```
NextDate(date, "unit")
```

> returns the next calendar unit, such as `"Day"`, `"Month"` or `"Year"`, after the one containing `date`.

```
NextDate(spec)
```

> uses the current date.

A day specification is one of the symbols `Monday`, `Tuesday`, ..., `Sunday`.

### Examples

June 22, 2024 is a Saturday:

```
>> NextDate({2024, 6, 22}, Sunday)
DateObject({2024,6,23},Day)
```

The day itself is not included:

```
>> NextDate({2024, 6, 23}, Sunday)
DateObject({2024,6,30},Day)
```

```
>> NextDate(DateObject({2024, 2, 28}), "Day")
DateObject({2024,2,29},Day)

>> NextDate(DateObject({2024, 12}), "Month")
DateObject({2025,1},Month)

>> NextDate(DateObject({2024, 2, 28}), "Year")
DateObject({2025},Year)
```

### Related terms
[DatePlus](DatePlus.md), [DayPlus](DayPlus.md), [DayRound](DayRound.md), [PreviousDate](PreviousDate.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of NextDate](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/DateTimeFunctions.java)

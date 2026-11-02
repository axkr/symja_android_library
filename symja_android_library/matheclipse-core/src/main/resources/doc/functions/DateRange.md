## DateRange

```
DateRange(date1, date2)
```

> returns the list of the dates from `date1` to `date2` in steps of one day.

```
DateRange(date1, date2, n)
```

> uses steps of `n` days.

```
DateRange(date1, date2, step)
```

> uses steps of the time quantity `step`, such as `Quantity(1, "Months")`, or of a calendar unit such as `"Month"`.

Date lists give date lists and `DateObject`s give `DateObject`s.

### Examples

```
>> DateRange({2024, 1, 1}, {2024, 1, 3})
{{2024,1,1,0,0,0.0},{2024,1,2,0,0,0.0},{2024,1,3,0,0,0.0}}

>> DateRange(DateObject({2024, 1, 1}), DateObject({2024, 1, 3}))
{DateObject({2024,1,1},Day),DateObject({2024,1,2},Day),DateObject({2024,1,3},Day)}
```

Other steps:

```
>> DateRange({2024, 1, 1}, {2024, 1, 10}, Quantity(3, "Days"))
{{2024,1,1,0,0,0.0},{2024,1,4,0,0,0.0},{2024,1,7,0,0,0.0},{2024,1,10,0,0,0.0}}

>> DateRange({2024, 1, 1}, {2024, 3, 1}, "Month")
{{2024,1,1,0,0,0.0},{2024,2,1,0,0,0.0},{2024,3,1,0,0,0.0}}

>> DateRange({2024, 1, 1, 12, 0, 0}, {2024, 1, 1, 15, 0, 0}, Quantity(1, "Hours"))
{{2024,1,1,12,0,0.0},{2024,1,1,13,0,0.0},{2024,1,1,14,0,0.0},{2024,1,1,15,0,0.0}}
```

### Related terms
[DateList](DateList.md), [DatePlus](DatePlus.md), [DayRange](DayRange.md), [Quantity](Quantity.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of DateRange](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/DateTimeFunctions.java)

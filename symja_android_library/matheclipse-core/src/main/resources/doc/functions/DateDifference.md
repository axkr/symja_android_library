## DateDifference

```
DateDifference(date1, date2)
```

> returns the number of days from `date1` to `date2` as a `Quantity`.

```
DateDifference(date1, date2, "unit")
```

> returns the difference in the calendar unit `"unit"`.

```
DateDifference(date)
```

> returns the number of days from the current date to `date`.

A date can be given as a date list `{y, m, d, h, min, s}` (trailing elements may be left out), a `DateObject`, a date string or a number of seconds since the beginning of January 1, 1900. The units are `"Year"`, `"Quarter"`, `"Month"`, `"Week"`, `"Day"`, `"Hour"`, `"Minute"` and `"Second"`. The result is negative if `date2` is before `date1`.

### Examples

```
>> DateDifference({2042, 1, 4}, {2057, 1, 1})
Quantity(5476,"Days")

>> DateDifference(DateObject({2020, 1, 11}), DateObject({2020, 1, 1}))
Quantity(-10,"Days")
```

Other units:

```
>> DateDifference({2024, 1, 1, 10, 0, 0}, {2024, 1, 1, 12, 30, 0}, "Hour")
Quantity(5/2,"Hours")

>> DateDifference({2023, 1, 1}, {2023, 3, 1}, "Month")
Quantity(2,"Months")

>> DateDifference({2024, 1, 1}, {2024, 3, 1}, "Week")
Quantity(8.57143,"Weeks")

>> DateDifference({1000, 1, 1}, {3000, 1, 1}, "Year")
Quantity(2000,"Years")
```

### Related terms
[DateList](DateList.md), [DateObject](DateObject.md), [DatePlus](DatePlus.md), [DayCount](DayCount.md), [Quantity](Quantity.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of DateDifference](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/DateTimeFunctions.java)

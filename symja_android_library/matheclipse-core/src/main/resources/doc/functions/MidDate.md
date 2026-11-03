## MidDate

```
MidDate({date1, date2})
```

> returns the date halfway between `date1` and `date2`.

```
MidDate(date)
```

> returns the middle of the period of a `DateObject` such as a month or a year.

```
MidDate(dates, "unit")
```

> returns the middle date rounded to the calendar unit `"unit"`.

```
MidDate(dates, "unit", r)
```

> returns the date at the fraction `r` of the way instead of halfway.

### Examples

```
>> MidDate({{2020, 1, 1}, {2020, 1, 3}})
DateObject({2020,1,2,12,0,0.0},Instant,Gregorian,0.0)

>> MidDate({{2024, 1, 1}, {2024, 1, 3}}, "Day")
DateObject({2024,1,2},Day)

>> MidDate({"1 January 2019", "31 December 2019"}, "Day")
DateObject({2019,7,2},Day)
```

The middle day of the year 2024:

```
>> MidDate(DateObject({2024}), "Day")
DateObject({2024,7,2},Day)
```

Two thirds of the way through the week which begins on September 30, 2024:

```
>> MidDate(DateObject({2024, 9, 30}, "Week"), "Hour", 2/3)
DateObject({2024,10,4,16},Hour,Gregorian,0.0)
```

### Related terms
[DateBounds](DateBounds.md), [DateObject](DateObject.md), [MaxDate](MaxDate.md), [MinDate](MinDate.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of MidDate](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/DateTimeFunctions.java)

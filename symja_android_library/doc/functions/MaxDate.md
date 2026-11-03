## MaxDate

```
MaxDate({date1, date2, ...})
```

> returns the latest of the dates.

```
MaxDate(interval)
```

> returns the end of the `DateInterval`.

Date lists give a date list and `DateObject`s a `DateObject`.

### Examples

```
>> MaxDate({{2024, 6, 1}, {2024, 1, 1}, {2024, 3, 15}})
{2024,6,1}

>> MaxDate({DateObject({2022, 4, 8}), DateObject({2022, 4, 9}), DateObject({2022, 4, 7})})
DateObject({2022,4,9},Day)

>> MaxDate(DateInterval({{2020, 1, 1}, {2021, 1, 1}}))
DateObject({2021,1,1},Day)
```

### Related terms
[DateBounds](DateBounds.md), [DateObject](DateObject.md), [MidDate](MidDate.md), [MinDate](MinDate.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of MaxDate](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/DateTimeFunctions.java)

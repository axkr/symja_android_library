## MinDate

```
MinDate({date1, date2, ...})
```

> returns the earliest of the dates.

```
MinDate(interval)
```

> returns the beginning of the `DateInterval`.

Date lists give a date list and `DateObject`s a `DateObject`.

### Examples

```
>> MinDate({{2024, 6, 1}, {2024, 1, 1}, {2024, 3, 15}})
{2024,1,1}

>> MinDate({DateObject({2022, 4, 8}), DateObject({2022, 4, 9}), DateObject({2022, 4, 7})})
DateObject({2022,4,7},Day)

>> MinDate(DateInterval({{2020, 1, 1}, {2021, 1, 1}}))
DateObject({2020,1,1},Day)
```

### Related terms
[DateBounds](DateBounds.md), [DateObject](DateObject.md), [MaxDate](MaxDate.md), [MidDate](MidDate.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of MinDate](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/DateTimeFunctions.java)

## DateBounds

```
DateBounds({date1, date2, ...})
```

> returns the earliest and the latest of the dates as a list `{min, max}`.

Date lists give date lists and `DateObject`s give `DateObject`s.

### Examples

```
>> DateBounds({{2024, 6, 1}, {2024, 1, 1}, {2024, 3, 15}})
{{2024,1,1},{2024,6,1}}

>> DateBounds({{2024, 1, 1, 12, 0, 0}, {2024, 1, 1, 6, 0, 0}})
{{2024,1,1,6,0,0},{2024,1,1,12,0,0}}

>> DateBounds({DateObject({2024, 1, 1}), DateObject({2024, 5, 1})})
{DateObject({2024,1,1},Day),DateObject({2024,5,1},Day)}
```

### Related terms
[DateObject](DateObject.md), [MaxDate](MaxDate.md), [MidDate](MidDate.md), [MinDate](MinDate.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of DateBounds](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/DateTimeFunctions.java)

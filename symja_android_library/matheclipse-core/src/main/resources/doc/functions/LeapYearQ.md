## LeapYearQ

```
LeapYearQ(date)
```

> returns `True` if `date` is in a leap year of the Gregorian calendar, and `False` otherwise.

A date can be given as a date list `{y, m, d, h, min, s}` (trailing elements may be left out), a `DateObject`, a date string or a number of seconds since the beginning of January 1, 1900. A year has to be given as the date list `{y}`; a plain integer is read as a number of seconds since 1900.

See
* [Wikipedia - Leap year](https://en.wikipedia.org/wiki/Leap_year)

### Examples

Years divisible by `100` are leap years only if they are divisible by `400`:

```
>> LeapYearQ({2024})
True

>> LeapYearQ({1900})
False

>> LeapYearQ({2000})
True
```

```
>> LeapYearQ("2024-03-01")
True

>> LeapYearQ(DateObject({2028, 5, 1}))
True
```

The leap years from 1896 to 1912:

```
>> Select(Range(1896, 1912), LeapYearQ({#})&)
{1896,1904,1908,1912}
```

`2024` is read as `2024` seconds after the beginning of 1900:

```
>> LeapYearQ(2024)
False
```

### Related terms
[DateList](DateList.md), [DateObject](DateObject.md), [DayCount](DayCount.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of LeapYearQ](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/DateTimeFunctions.java)

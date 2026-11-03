## DatePlus

```
DatePlus(date, n)
```

> adds `n` days to `date`.

```
DatePlus(date, {n, "unit"})
```

> adds `n` of the calendar unit `"unit"` to `date`.

```
DatePlus(date, {{n1, "unit1"}, {n2, "unit2"}, ...})
```

> adds several increments one after the other.

```
DatePlus(date, Quantity(n, "unit"))
```

> adds the time quantity to `date`.

```
DatePlus(increment)
```

> adds the increment to the current date.

The units are `"Year"`, `"Quarter"`, `"Month"`, `"Week"`, `"Day"`, `"Hour"`, `"Minute"` and `"Second"`. Adding whole months or years keeps the day of the month, and moves it back to the last day of a shorter month. A date list gives a date list and a `DateObject` gives a `DateObject`, so a time quantity can also be added with `+`.

### Examples

```
>> DatePlus({2010, 2, 5}, 73)
{2010,4,19}

>> DatePlus({2010, 2, 5}, {{8, "Week"}, {1, "Day"}})
{2010,4,3}
```

One month after January 31 in a leap year:

```
>> DatePlus({2020, 1, 31}, {1, "Month"})
{2020,2,29}
```

The last day of February 2013:

```
>> DatePlus({2013, 2}, {{1, "Month"}, {-1, "Day"}})
{2013,2,28}
```

Time quantities:

```
>> DatePlus({2026, 4, 1}, Quantity(2, "Years"))
{2028,4,1}

>> DatePlus(DateObject({2024, 7, 4}), Quantity(3, "Weeks"))
DateObject({2024,7,25},Day)

>> DateObject({2024, 7, 4}) + Quantity(2, "Months")
DateObject({2024,9,4},Day)
```

### Related terms
[DateDifference](DateDifference.md), [DateList](DateList.md), [DateObject](DateObject.md), [DateRange](DateRange.md), [DayPlus](DayPlus.md), [NextDate](NextDate.md), [Quantity](Quantity.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of DatePlus](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/DateTimeFunctions.java)

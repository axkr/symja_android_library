## DateObjectQ

```
DateObjectQ(expr)
```

> returns `True` if `expr` is a `DateObject`, and `False` otherwise.

### Examples

```
>> DateObjectQ(DateObject({2026, 7, 4}))
True

>> DateObjectQ(Today)
True
```

A date list, a date string and a `TimeObject` are no `DateObject`s:

```
>> DateObjectQ({2026, 7, 4})
False

>> DateObjectQ("2026-07-04")
False

>> DateObjectQ(TimeObject({12, 30}))
False
```

### Related terms
[DateObject](DateObject.md), [TimeObject](TimeObject.md)

### Implementation status

* &#x2705; - supported

### Github

* [Implementation of DateObjectQ](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/DateTimeFunctions.java)

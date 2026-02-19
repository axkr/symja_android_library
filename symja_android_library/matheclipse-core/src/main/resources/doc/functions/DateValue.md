## DateValue

```
DateValue("date-time-string")
```

> return the current date in the specified date-time form

```
DateValue(date-object, {"date-time-str1","date-time-str2",...})
```

> return the date object as a list in the specified date-time forms

### Examples

```
>> DateValue(DateObject({2016,8,1}), {"Year","Month","Day"})
{2016,8,1}
```

### Implementation status

* &#x1F9EA; - experimental

### Github

* [Implementation of DateValue](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/QuantityFunctions.java#L196) 

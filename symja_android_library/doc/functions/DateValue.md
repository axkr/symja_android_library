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
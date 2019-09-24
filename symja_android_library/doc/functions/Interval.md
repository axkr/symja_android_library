## Interval
 
```
Interval({a, b})
```

> represents the interval from `a` to `b`.


See 
* [Wikipedia - Interval (mathematics)](https://en.wikipedia.org/wiki/Interval_(mathematics))

### Examples

```
>> Interval({1, 6}) * Interval({0, 2}) 
Interval({0,12})

>> Interval({1.5, 6}) * Interval({0.1, 2.7})
Interval({0.15,16.2})
```

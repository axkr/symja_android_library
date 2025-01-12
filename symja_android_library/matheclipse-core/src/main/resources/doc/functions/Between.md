## Between

```
Between(expr, {min, max}) 

Between(expr, Interval({min, max})) 
```

> equivalent to `min <= expr <= max`.

```
Between(expr, {{min1, max1}, {min2, max2},...}) 

Between(expr, Interval({min1, max1}, {min2, max2},...)) 
```

> equivalent to `(min1 <= expr <= max1) || (min2 <= expr <= max2) || ...`.

```
Between({min, max}) 

Between({{min1, max1}, {min2, max2},...)
```

> operator form that yields `Between(expr, range)` when applied to expression `expr` for `{min, max},...` ranges. 

See 
* [Wikipedia - Interval arithmetic](https://en.wikipedia.org/wiki/Interval_arithmetic)
* [Wikipedia - Interval (mathematics)](https://en.wikipedia.org/wiki/Interval_(mathematics))


### Examples
 
Check that `6` is in range `4..10`:
 
```
>> Between(6, {4, 10})
True
```

Same as above in operator form:

```
>> Between({4, 10})[6]
True
     
>> Between({4, 10})[206]
False
```

 
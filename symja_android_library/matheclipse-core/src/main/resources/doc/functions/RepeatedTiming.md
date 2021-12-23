## RepeatedTiming

```
RepeatedTiming(x)
```

> returns a list with the first entry containing the average evaluation time of `x` and the second entry containing the evaluation result of `x`.

### Examples

```
>> RepeatedTiming(FactorInteger(2^32-1))
{0.0003,{{3,1},{5,1},{17,1},{257,1},{65537,1}}}
```

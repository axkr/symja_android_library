## AbsoluteTiming

```
AbsoluteTiming(x)
```

> returns a list with the first entry containing the evaluation time of `x` and the second entry is the evaluation result of `x`.

### Examples

```
>> AbsoluteTiming(x = 1; Pause(x); x + 3)[[1]] > 1
True
```

### Related terms 
[Pause](Pause.md), [TimeConstrained](TimeConstrained.md), [Timing](Timing.md)



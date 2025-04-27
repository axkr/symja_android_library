## Timing

```
Timing(x)
```

> returns a list with the first entry containing the evaluation CPU time of `x` and the second entry is the evaluation result of `x`.

**Note**: if the Java Management Extensions (JMX) library is not available, this function switch's to `AbsoluteTiming` function.

### Examples

```
>> Timing(FactorInteger(3225275494496681))
{0.547,{{56791489,1},{56791529,1}}}
```

Suppress the output of the result:

```
>> Timing(FactorInteger(3225275494496681);)
{0.393,Null}
```

### Related terms 
[AbsoluteTiming](AbsoluteTiming.md), [Pause](Pause.md), [TimeConstrained](TimeConstrained.md)



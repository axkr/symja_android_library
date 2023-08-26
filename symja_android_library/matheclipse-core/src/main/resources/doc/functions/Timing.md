## Timing

```
Timing(x)
```

> returns a list with the first entry containing the evaluation CPU time of `x` and the second entry is the evaluation result of `x`.

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
[Pause](Pause.md), [TimeConstrained](TimeConstrained.md)<






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Timing](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Programming.java#L3179) 

## RamseyNumber

```
RamseyNumber(r, s)
```

> returns the Ramsey number `R(r,s)`. Currently not all values are known for `1 <= r <= 4`. The function returns unevaluated if the value is unknown.

See
* [Wikipedia - Ramsey's theorem](https://en.wikipedia.org/wiki/Ramsey's_theorem)
* [OEIS - A212954](https://oeis.org/A212954)

### Examples

```
>> Table(RamseyNumber(1,j), {j,1,20})
{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}

>> Table(RamseyNumber(2,j), {j,1,20})
{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20}
        
>> Table(RamseyNumber(i,j), {i,1,5},{j,i,10})
{{1,1,1,1,1,1,1,1,1,1},{2,3,4,5,6,7,8,9,10},{6,9,14,18,23,28,36,RamseyNumber(3,10)},{18,25,RamseyNumber(4,6),RamseyNumber(4,7),RamseyNumber(4,8),RamseyNumber(4,9),RamseyNumber(4,10)}}
```

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of RamseyNumber](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/NumberTheory.java#L5480) 

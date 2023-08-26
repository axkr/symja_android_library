## KroneckerDelta

```
KroneckerDelta(arg1, arg2, ... argN)
```
> if all arguments `arg1` to `argN` are equal return `1`, otherwise return `0`. 
 

See  
* [Wikipedia - Kronecker delta](https://en.wikipedia.org/wiki/Kronecker_delta)
 
### Examples

```
>> KroneckerDelta(42)
0

>> KroneckerDelta(42, 42.0, 42)
1
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of KroneckerDelta](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/PiecewiseFunctions.java#L410) 

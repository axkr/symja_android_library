## ConstantArray

```
ConstantArray(expr, n)
```
> returns a list of `n` copies of `expr`.

### Examples

```
>> ConstantArray(a, 3)
{a, a, a}
 
>> ConstantArray(a, {2, 3})
{{a, a, a}, {a, a, a}}
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of ConstantArray](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ListFunctions.java#L1776) 

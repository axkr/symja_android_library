## Trace

```
Trace(expr)
```

> return the evaluation steps which are used to get the result.
 
### Examples

```
>> Trace(D(Sin(x),x))
{{Cos(#1)&[x],Cos(x)},D(x,x)*Cos(x),{D(x,x),1},1*Cos(x),Cos(x)}
```

### Related terms
[Stack](Stack.md), [StackBegin](StackBegin.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Trace](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Programming.java#L3264) 

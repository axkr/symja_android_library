## RightComposition

```
RightComposition(sym1, sym2,...)[arg1, arg2,...]
```

> creates a composition of the symbols applied in reversed order at the arguments.


### Examples

```
>> RightComposition(u, v, w)[x, y]
w(v(u(x,y)))
```







### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of RightComposition](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ListFunctions.java#L6275) 

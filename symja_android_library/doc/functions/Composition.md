## Composition

```
Composition(sym1, sym2,...)[arg1, arg2,...]
```

> creates a composition of the symbols applied at the arguments.


### Examples

```
>> Composition(u, v, w)[x, y]
u(v(w(x,y)))
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Composition](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ListFunctions.java#L1666) 

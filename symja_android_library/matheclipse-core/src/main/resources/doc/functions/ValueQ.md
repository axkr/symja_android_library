## ValueQ

```
ValueQ(expr) 
```

> returns `True` if and only if `expr` is defined.

### Examples
 
```
>> ValueQ(x)
False

>> x=1;

>> ValueQ(x)
True

>> ValueQ(True)
False
```
 






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of ValueQ](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/PredicateQ.java#L1569) 

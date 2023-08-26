## MatchQ

```
MatchQ(expr, form)
```

> tests whether `expr` matches `form`.
 
### Examples

```
>> MatchQ(123, _Integer)
True
	 
>> MatchQ(123, _Real)
False
	 
>> MatchQ(_Integer)[123]
True
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of MatchQ](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/PredicateQ.java#L663) 

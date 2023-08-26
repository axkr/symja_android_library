## ArrayQ

```
ArrayQ(expr)
```

> tests whether expr is a full array.
	
```
ArrayQ(expr, pattern)
```

> also tests whether the array depth of expr matches pattern.
	
```
ArrayQ(expr, pattern, test)
```

> furthermore tests whether `test` yields `True` for all elements of expr. 
 
### Examples

```
>> ArrayQ(a)
False

>> ArrayQ({a})
True

>> ArrayQ({{{a}},{{b,c}}})
False

>> ArrayQ({{a, b}, {c, d}}, 2, SymbolQ)
True
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of ArrayQ](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/PredicateQ.java#L220) 

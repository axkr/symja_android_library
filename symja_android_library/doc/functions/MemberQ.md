## MemberQ

```
MemberQ(list, pattern)
```

> returns `True` if pattern matches any element of `list`, or `False` otherwise.

### Examples
 
```
>> MemberQ({a, b, c}, b)
True
>> MemberQ({a, b, c}, d)
False
>> MemberQ({"a", b, f(x)}, _?NumericQ)
False
>> MemberQ(_List)({{}})
True
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of MemberQ](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/PredicateQ.java#L818) 

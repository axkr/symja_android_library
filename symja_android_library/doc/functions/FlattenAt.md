## FlattenAt

```
FlattenAt(expr, position)
```

> flattens out nested lists at the given `position` in `expr`.
	 

### Examples

```
>> FlattenAt(f(a, g(b,c), {d, e}, {f}), -2)
f(a,g(b,c),d,e,{f})

>> FlattenAt(f(a, g(b,c), {d, e}, {f}), 4)
f(a,g(b,c),{d,e},f)
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of FlattenAt](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/StructureFunctions.java#L573) 

## LevelQ

```
LevelQ(expr)
```

> tests whether `expr` is a valid level specification.
	
### Examples

```
>> LevelQ(2)
True

>> LevelQ({2, 4})
True

>> LevelQ(Infinity)
True

>> LevelQ(a + b)
False
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of LevelQ](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ListFunctions.java#L4211) 

## Optional

```
Optional(patt, default)
```

or

```
patt : default
```

> is a pattern which matches `patt`, which if omitted should be replaced by `default`.
	 
### Examples

```
>> f(x_, y_:1) := {x, y}

>> f(1, 2)
{1,2}

>> f(a)
{a,1}
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Optional](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/PatternMatching.java#L1134) 

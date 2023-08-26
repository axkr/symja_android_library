## PatternTest

```
PatternTest(pattern, test)
```

or

```
pattern ? test
```

> constrains `pattern` to match `expr` only if the evaluation of `test(expr)` yields `True`.
 
### Examples

```
>> MatchQ(3, _Integer?(#>0&))
True
	 
>> MatchQ(-3, _Integer?(#>0&))
False
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of PatternTest](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/PatternMatching.java#L1413) 

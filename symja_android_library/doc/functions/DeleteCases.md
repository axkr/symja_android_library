## DeleteCases

```
DeleteCases(list, pattern)
```
> returns the elements of `list` that do not match `pattern`.

### Examples
```
>> DeleteCases({a, 1, 2.5, "string"}, _Integer|_Real)
{a,"string"}
 
>> DeleteCases({a, b, 1, c, 2, 3}, _Symbol)
{1,2,3}
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of DeleteCases](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ListFunctions.java#L2135) 

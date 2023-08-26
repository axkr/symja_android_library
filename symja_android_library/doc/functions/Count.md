## Count
```
Count(list, pattern)
```
> returns the number of times `pattern` appears in `list`.

```
Count(list, pattern, ls)
```
> counts the elements matching at levelspec `ls`.

### Examples
```
>> Count({3, 7, 10, 7, 5, 3, 7, 10}, 3)
2
 
>> Count({{a, a}, {a, a, a}, a}, a, {2})
5
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Count](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ListFunctions.java#L1881) 

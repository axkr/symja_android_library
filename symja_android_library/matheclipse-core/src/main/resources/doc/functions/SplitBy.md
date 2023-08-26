## SplitBy

```
SplitBy(list, f)
```

> splits `list` into collections of consecutive elements that give the same result when `f` is applied.

### Examples

```
>> SplitBy(Range(1, 3, 1/3), Round) 
{{1,4/3},{5/3,2,7/3},{8/3,3}}
{{1, 4 / 3}, {5 / 3, 2, 7 / 3}, {8 / 3, 3}}
 
>> SplitBy({1, 2, 1, 1.2}, {Round, Identity})
{{{1}},{{2}},{{1},{1.2}}} 
 
>> SplitBy(Tuples({1, 2}, 3), First)
{{{1,1,1},{1,1,2},{1,2,1},{1,2,2}},{{2,1,1},{2,1,2},{2,2,1},{2,2,2}}} 
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of SplitBy](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ListFunctions.java#L6794) 

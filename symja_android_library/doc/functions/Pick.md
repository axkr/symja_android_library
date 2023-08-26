## Pick

``` 
Pick(nested-list, nested-selection)
```
> returns the elements of `nested-list` that have value `True` in the corresponding position in `nested-selection`.

``` 
Pick(nested-list, nested-selection, pattern)
```
> returns the elements of `nested-list` those values in the corresponding position in `nested-selection` match the `pattern`.

### Examples

```
>> Pick({{1, 2}, {2, 3}, {5, 6}}, {{1}, {2, 3}, {{3, 4}, {4, 5}}}, {1} | 2 | {4, 5}) 
{{1,2},{2},{6}}
```

### Related terms 
[Cases](Cases.md), [Select](Select.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Pick](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ListFunctions.java#L4875) 

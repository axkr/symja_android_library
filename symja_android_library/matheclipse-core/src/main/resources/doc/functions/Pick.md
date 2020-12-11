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
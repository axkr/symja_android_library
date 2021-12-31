## TakeWhile

```
TakeWhile({e1, e2, ...}, head)
```

> returns the list of elements `ei` at the start of list for which `head(ei)` returns `True`.

### Examples


```
>> TakeWhile({1, 2, 3, 10, 5, 8, 42, 11}, # < 10 &)
{1,2,3}
```

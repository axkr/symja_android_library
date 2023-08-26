## Complement 

``` 
Complement(set1, set2)
```

> get the complement set from `set1` and `set2`.

See 
* [Wikipedia - Complement (set theory)](https://en.wikipedia.org/wiki/Complement_(set_theory)) 

### Examples

```
>> Complement({1,2,3},{2,3,4})
{1}

>> Complement({2,3,4},{1,2,3})
{4}
```

In the line below we get all elements that are in `{3, 2, 7, 5, 2, 2, 3, 4, 5, 6, 1}` but not in `{2, 3}` or `{4, 6, 27, 23}`. 
Of course this would work with lists of arbitrary expressions, not only numbers.

```
>> Complement({3, 2, 7, 5, 2, 2, 3, 4, 5, 6, 1}, {2, 3}, {4, 6, 27, 23})
{1,5,7}
```

### Related terms 
[Intersection](Intersection.md), [Union](Union.md)  






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Complement](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ListFunctions.java#L1597) 

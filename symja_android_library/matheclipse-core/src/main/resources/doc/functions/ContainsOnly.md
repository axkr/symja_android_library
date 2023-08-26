## ContainsOnly

```
ContainsOnly(list1, list2)
```

> yields True if `list1` contains only elements that appear in `list2`.


### Examples

```
>> ContainsOnly({b, a, a}, {a, b, c})
True
```

The first list contains elements not present in the second list:
    
```
>> ContainsOnly({b, a, d}, {a, b, c})
False

>> ContainsOnly({}, {a, b, c})
True
```
 








### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of ContainsOnly](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ContainsFunctions.java#L132) 

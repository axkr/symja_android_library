## RandomSample

```
RandomSample(items)
```

> create a random sample for the arguments of the `items`.

```
RandomSample(items, n)
```

> randomly picks `n` items from items. Each pick in the `n` picks happens after the previous items picked have been removed from items, so each item can be picked at most once.

### Examples

```
>> RandomSample(f(1,2,3,4,5))
f(3,4,5,1,2)

>> RandomSample(f(1,2,3,4,5),3)
f(3,4,1)
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of RandomSample](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/RandomFunctions.java#L763) 

## RandomChoice

```
RandomChoice({item1, item2, item3,...})
```

> randomly picks one `item` from items. 
 
```
RandomChoice({item1, item2, item3,...}, n)
```

> randomly picks `n` items from items. Each pick in the `n` picks happens from the given set of items, so each item can be picked any number of times.

```
RandomChoice({item1, item2, item3,...}, {n1, n2, ...})
```
 
> randomly picks items from items and arranges the picked items in the nested list structure described by {n1, n2, ...}.

```
RandomChoice(weights -> items, n)
```

> randomly picks n items from items and uses the corresponding numeric values in weights to determine how probable it is for each item in items to get picked (in the long run, items with higher weights will get picked more often than ones with lower weight).

```
RandomChoice[weights -> items]
```

> randomly picks one item from `items` using weights `weights`.

```
RandomChoice(weights -> items, {n1, n2,...})
```

> randomly picks a structured list of items from `items` using weights `weights`.

### Examples

```
>> RandomChoice({1,2,3,4,5,6,7})
5

>> RandomChoice({a,b,c},20)
{b,a,b,b,b,c,b,c,c,b,a,a,a,c,b,a,b,a,b,c}

>> RandomChoice({a, b, c}, {5, 2})
{{c,a},{c,c},{c,a},{c,c},{a,a}}
      
>> RandomChoice({1, 10, 5} -> {a, b, c}, 20)
{c,a,a,b,c,c,b,b,a,c,b,b,c,b,b,c,b,b,b,b}
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of RandomChoice](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/RandomFunctions.java#L123) 

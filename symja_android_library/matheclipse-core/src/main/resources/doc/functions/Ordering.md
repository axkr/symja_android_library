## Ordering

```
Ordering(list)
```

> calculate the permutation list of the elements in the sorted `list`.

```
Ordering(list, n)
```

> calculate the first `n` indexes of the  permutation list of the elements in the sorted `list`.

```
Ordering(list, -n)
```

> calculate the last `n` indexes of the  permutation list of the elements in the sorted `list`.


```
Ordering(list, n, head)
```

> calculate the first `n` indexes of the  permutation list of the elements in the sorted `list` using comparator operation `head`.

See
* [Wikipedia - Order theory](https://en.wikipedia.org/wiki/Order_theory)

### Examples

```
>> Ordering({1,3,4,2,5,9,6})
{1,4,2,3,5,7,6}

>> Ordering({1,3,4,2,5,9,6}, All, Greater)
{6,7,5,3,2,4,1}
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Ordering](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/TensorFunctions.java#L550) 

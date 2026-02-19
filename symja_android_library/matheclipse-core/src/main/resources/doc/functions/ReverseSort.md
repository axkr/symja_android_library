## ReverseSort

```
ReverseSort(list)
```

> sorts `list` (or the leaves of any other expression) according to reversed canonical ordering.

```
ReverseSort(list, p) 
```

> sorts in reversed order using `p` to determine the order of two elements.
 
### Examples

```
>> ReverseSort({4, 1.0, a, 3+I})
{a,4,3+I,1.0}
```

### Related terms 
[NumericalOrder](NumericalOrder.md), [NumericalSort](NumericalSort.md), [Order](Order.md), [Sort](Sort.md)


### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of ReverseSort](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/StructureFunctions.java#L1720) 

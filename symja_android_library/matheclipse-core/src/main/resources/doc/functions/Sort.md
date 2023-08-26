## Sort

```
Sort(list)
```

> sorts `list` (or the leaves of any other expression) according to canonical ordering.

```
Sort(list, p) 
```

> sorts using `p` to determine the order of two elements.
 
### Examples

```
>> Sort({4, 1.0, a, 3+I})
{1.0,4,3+I,a}
```

### Related terms 
[NumericalOrder](NumericalOrder.md), [NumericalSort](NumericalSort.md), [Order](Order.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Sort](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/StructureFunctions.java#L1886) 

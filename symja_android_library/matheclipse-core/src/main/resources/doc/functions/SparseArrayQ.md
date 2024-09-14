## SparseArrayQ

```
SparseArrayQ(expr)
```

> return `True` if `expr` is a sparse array.

### Examples

``` 
>> s = SparseArray({{1, 1} -> 1, {2, 3} -> 4, {3, 1} -> -1}); 
 
>> SparseArrayQ(s)
True
```

### Related terms  
[SparseArray](SparseArray.md), [MatrixForm.md](MatrixForm.md), [Normal](Normal.md)

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of SparseArrayQ](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/PredicateQ.java#L1337) 

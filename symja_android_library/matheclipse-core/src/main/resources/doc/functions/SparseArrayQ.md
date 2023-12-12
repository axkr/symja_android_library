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
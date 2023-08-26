## SparseArray

```
SparseArray(nested-list)
```

> create a sparse array from a `nested-list` structure.

```
SparseArray(array-rules, list-of-integers, default-value)
```

> create a sparse array from `array-rules` with dimension `list-of-integers` and undefined elements are having `default-value`.

```
SparseArray(Automatic, list-of-integers, default-value, crs-list)
```

> create a sparse array from the compressed-row-storage `crs-list` with dimension `list-of-integers` and undefined elements are having `default-value`.

See
* [Netlib - Compressed Row Storage (CRS)](http://netlib.org/utk/papers/templates/node91.html)

### Examples

``` 
>> SparseArray({{1, 1} -> 1, {2, 2} -> 2, {3, 3} -> 3, {1, 3} -> 4}) 
SparseArray(Number of elements: 4 Dimensions: {3,3} Default value: 0)

>> SparseArray({{1, 1} -> 1, {2, 2} -> 2, {3, 3} -> 3, {1, 3} -> 4}, Automatic, 0)
SparseArray(Number of elements: 4 Dimensions: {3,3} Default value: 0)
```

### Related terms  
[MatrixForm](MatrixForm.md), [Normal](Normal.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of SparseArray](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/SparseArrayFunctions.java#L171) 

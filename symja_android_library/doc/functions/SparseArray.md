## SparseArray

```
SparseArray(nestedList)
```

> create a sparse array from a `nestedList` structure.

```
SparseArray(arrayRules, listOfIntegers, defaultValue)
```

> create a sparse array from `arrayRules` with dimension `listOfIntegers` and undefined elements are having `defaultValue`.

```
SparseArray(Automatic, listOfIntegers, defaultValue, crsList)
```

> create a sparse array from the compressed-row-storage `crsList` with dimension `listOfIntegers` and undefined elements are having `defaultValue`.

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
[SparseArrayQ](SparseArrayQ.md), [MatrixForm](MatrixForm.md), [Normal](Normal.md)






### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of SparseArray](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/SparseArrayFunctions.java#L174) 

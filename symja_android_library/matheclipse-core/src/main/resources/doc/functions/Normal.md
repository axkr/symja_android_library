## Normal

```
Normal(expr)
```

> converts a Symja expression `expr` into a normal expression.

### Examples

Normalize a series expression:

```
>> Normal(SeriesData(x, 0, {1, 0, -1, -4, -17, -88, -549}, -1, 6, 1))
1/x-x-4*x^2-17*x^3-88*x^4-549*x^5
```

Normalize a byte array expression:

```
>> BinarySerialize(f(g,2)) // Normal
{56,58,102,2,115,8,71,108,111,98,97,108,96,102,115,8,71,108,111,98,97,108,96,103,67,2}
```

Normalize a sparse array:

```
>> s=SparseArray({11 -> a, 17 -> b}) 
SparseArray(Number of elements: 2 Dimensions: {17} Default value: 0)

>> Normal(s) 
{0,0,0,0,0,0,0,0,0,0,a,0,0,0,0,0,b}
```

Normalize an association:

```
>> assoc = AssociationThread({"U","V"},{1,2})
<|U->1,V->2|>
				
>> Normal(assoc)
{U->1,V->2}
```

### Related terms  
[Association](Association.md), [SeriesData](SeriesData.md) , [SparseArray](SparseArray.md) 






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Normal](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/SeriesFunctions.java#L997) 

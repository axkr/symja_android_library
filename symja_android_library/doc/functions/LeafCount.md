## LeafCount

```
LeafCount(expr)
```

> returns the total number of indivisible subexpressions in `expr`.

### Examples

```
>> LeafCount(1 + x + y^a) 
6

>> LeafCount(f(x, y)) 
3 

>> LeafCount({1 / 3, 1 + I}) 
7 

>> LeafCount(Sqrt(2)) 
5 

>> LeafCount(100!) 
1 

>> LeafCount(f(1, 2)[x, y]) 
5 

>> LeafCount(10+I) 
3
```

### Related terms 
[FullForm](FullForm.md) 
 






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of LeafCount](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/StructureFunctions.java#L791) 

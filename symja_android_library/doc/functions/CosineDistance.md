## CosineDistance

```
CosineDistance(u, v)
```
> returns the cosine distance between `u` and `v`.
  
### Examples

```
>> N(CosineDistance({7, 9}, {71, 89}))
7.596457213221441E-5
 
>> CosineDistance({a, b}, {c, d})
1-(a*c+b*d)/(Sqrt(Abs(a)^2+Abs(b)^2)*Sqrt(Abs(c)^2+Abs(d)^2))  
```

### Related terms 
[FindClusters](FindClusters.md), [BinaryDistance](BinaryDistance.md), [BrayCurtisDistance](BrayCurtisDistance.md), [ChessboardDistance](ChessboardDistance.md), [CanberraDistance](CanberraDistance.md), [EuclideanDistance](EuclideanDistance.md), [ManhattanDistance](ManhattanDistance.md), [SquaredEuclideanDistance](SquaredEuclideanDistance.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of CosineDistance](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ClusteringFunctions.java#L362) 

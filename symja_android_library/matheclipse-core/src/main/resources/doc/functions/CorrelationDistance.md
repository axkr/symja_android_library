## CorrelationDistance

```
CorrelationDistance(u, v)
```
> returns the correlation distance between `u` and `v`.
  
### Examples

```
>> CorrelationDistance({a, b}, {c, d})
1+(-a*c+b*c+a*d-b*d)/(2*Sqrt(Abs(a+1/2*(-a-b))^2+Abs(1/2*(-a-b)+b)^2)*Sqrt(Abs(c+1/2*(-c-d))^2+Abs(1/2*(-c-d)+d)^2))
```

### Related terms 
[FindClusters](FindClusters.md), [BinaryDistance](BinaryDistance.md), [BrayCurtisDistance](BrayCurtisDistance.md), [ChessboardDistance](ChessboardDistance.md), [CanberraDistance](CanberraDistance.md), [CosineDistance](CosineDistance.md), [EuclideanDistance](EuclideanDistance.md), [ManhattanDistance](ManhattanDistance.md), [SquaredEuclideanDistance](SquaredEuclideanDistance.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of CorrelationDistance](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ClusteringFunctions.java#L241) 

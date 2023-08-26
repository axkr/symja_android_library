## EuclideanDistance
```
EuclideanDistance(u, v)
```

> returns the euclidean distance between `u` and `v`.

See
* [Wikipedia - Euclidean distance](https://en.wikipedia.org/wiki/Euclidean_distance)

### Examples

```
>> EuclideanDistance({-1, -1}, {1, 1})
2*Sqrt(2)

>> EuclideanDistance({a, b}, {c, d})
Sqrt(Abs(a-c)^2+Abs(b-d)^2) 
```

### Related terms 
[FindClusters](FindClusters.md), [BinaryDistance](BinaryDistance.md), [BrayCurtisDistance](BrayCurtisDistance.md), [ChessboardDistance](ChessboardDistance.md), [CanberraDistance](CanberraDistance.md), [CosineDistance](CosineDistance.md),  [ManhattanDistance](ManhattanDistance.md), [SquaredEuclideanDistance](SquaredEuclideanDistance.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of EuclideanDistance](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ClusteringFunctions.java#L417) 

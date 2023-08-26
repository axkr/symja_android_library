## CanberraDistance

```
CanberraDistance(u, v)
```

> returns the canberra distance between `u` and `v`, which is a weighted version of the Manhattan distance.
	
	
See
* [Wikipedia - Canberra distance](https://en.wikipedia.org/wiki/Canberra_distance)

### Examples

``` 
>> CanberraDistance({-1, -1}, {1, 1})
2
```

### Related terms 
[FindClusters](FindClusters.md), [BinaryDistance](BinaryDistance.md), [BrayCurtisDistance](BrayCurtisDistance.md), [ChessboardDistance](ChessboardDistance.md), [CosineDistance](CosineDistance.md), [EuclideanDistance](EuclideanDistance.md), [ManhattanDistance](ManhattanDistance.md), [SquaredEuclideanDistance](SquaredEuclideanDistance.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of CanberraDistance](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ClusteringFunctions.java#L170) 

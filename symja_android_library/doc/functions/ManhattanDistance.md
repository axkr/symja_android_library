## ManhattanDistance

```
ManhattanDistance(u, v)
```

> returns the Manhattan distance between `u` and `v`, which is the number of horizontal or vertical moves in the grid like Manhattan city layout to get from `u` to `v`.

See:
* [Wikipedia - Taxicab geometry](https://en.wikipedia.org/wiki/Taxicab_geometry)

### Examples

```
>> ManhattanDistance({-1, -1}, {1, 1})
4
```

### Related terms 
[FindClusters](FindClusters.md), [BinaryDistance](BinaryDistance.md), [BrayCurtisDistance](BrayCurtisDistance.md), [ChessboardDistance](ChessboardDistance.md), [CanberraDistance](CanberraDistance.md), [CosineDistance](CosineDistance.md), [EuclideanDistance](EuclideanDistance.md), [SquaredEuclideanDistance](SquaredEuclideanDistance.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of ManhattanDistance](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ClusteringFunctions.java#L604) 

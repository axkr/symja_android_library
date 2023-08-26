## ChessboardDistance

```
ChessboardDistance(u, v)
```

> returns the chessboard distance (also known as Chebyshev distance) between `u` and `v`, which is the number of moves a king on a chessboard needs to get from square `u` to square `v`.


See:  
* [Wikipedia - Chebyshev distance](https://en.wikipedia.org/wiki/Chebyshev_distance)


### Examples

```
>> ChessboardDistance({-1, -1}, {1, 1})
2
```

### Related terms 
[FindClusters](FindClusters.md), [BinaryDistance](BinaryDistance.md), [BrayCurtisDistance](BrayCurtisDistance.md), [CanberraDistance](CanberraDistance.md), [CosineDistance](CosineDistance.md), [EuclideanDistance](EuclideanDistance.md), [ManhattanDistance](ManhattanDistance.md), [SquaredEuclideanDistance](SquaredEuclideanDistance.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of ChessboardDistance](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ClusteringFunctions.java#L219) 

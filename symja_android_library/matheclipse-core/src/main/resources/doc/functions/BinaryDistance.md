## BinaryDistance

```
BinaryDistance(u, v)
```

> returns the binary distance between `u` and `v`. `0` if  `u` and `v` are unequal. `1` if `u` and `v` are equal.

### Examples

``` 
>> BinaryDistance({1, 2, 3, 4, 5}, {1, 2, 3, 4, 5})
1

>> BinaryDistance({1, 2, 3, 4, 5}, {1, 2, 3, 4, -5}) 
0

>> BinaryDistance({1, 2, 3, 4, 5}, {1, 2, 3, 4, 5.0}) 
0
```

### Related terms 
[FindClusters](FindClusters.md), [BrayCurtisDistance](BrayCurtisDistance.md), [ChessboardDistance](ChessboardDistance.md), [CanberraDistance](CanberraDistance.md), [CosineDistance](CosineDistance.md), [EuclideanDistance](EuclideanDistance.md), [ManhattanDistance](ManhattanDistance.md), [SquaredEuclideanDistance](SquaredEuclideanDistance.md)
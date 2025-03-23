## NearestTo

```
NearestTo(x-value)  
```

> returns the value from the `list-of-values` which is nearest to `x-value`. By default the `EuclideanDistance` is used. With the `DistanceFunction` option you can specify your own distance function.

### Examples
 
```
>> NearestTo(9)[{1, 2, 3, 5, 7, 11, 13, 17, 19}] 
{7,11}
```



### Related terms 
[FindClusters](FindClusters.md), [BinaryDistance](BinaryDistance.md), [BrayCurtisDistance](BrayCurtisDistance.md), [ChessboardDistance](ChessboardDistance.md), [CanberraDistance](CanberraDistance.md), [CosineDistance](CosineDistance.md), [EuclideanDistance](EuclideanDistance.md), [ManhattanDistance](ManhattanDistance.md), [Nearest](Nearest.md), [SquaredEuclideanDistance](SquaredEuclideanDistance.md)

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of NearestTo](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ListFunctions.java#L4440) 

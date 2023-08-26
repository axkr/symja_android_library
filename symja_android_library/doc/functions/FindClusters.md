## FindClusters

```
FindClusters(list-of-data-points, k)
```
 
> Clustering algorithm based on David Arthur and Sergei Vassilvitski k-means++ algorithm. Create `k` number of clusters to split the `list-of-data-points` into.

```
FindClusters(list-of-data-points, eps, minPts, Method->"DBSCAN", DistanceFunction->EuclideanDistance)
```

> Use the **density-based spatial clustering of applications with noise (DBSCAN)** algorithm as clustering `Method`. `eps` is the maximum radius of the neighborhood to be considered. `minPts` is the minimum number of points needed for a cluster. The `DistanceFunction` defines the function which should be used to measure the distance between 2 points.
   
See:  
* [Wikipedia - K-means++](https://en.wikipedia.org/wiki/K-means%2B%2B)
* [Wikipedia - K-means clustering](https://en.wikipedia.org/wiki/K-means_clustering) 
* [Wikipedia - DBSCAN](https://en.wikipedia.org/wiki/DBSCAN)
 
### Examples

```
>> FindClusters({1, 2, 3, 4, 5, 6, 7, 8, 9}) 
{{1.0,2.0,3.0},{6.0,7.0,8.0,9.0},{4.0,5.0}}

>> FindClusters({{83.08303244924173,58.83387754182331},{45.05445510940626,23.469642649637535},{14.96417921432294,69.0264096390456},{73.53189604333602,34.896145021310076},{73.28498173551634,33.96860806993209},{73.45828098873608,33.92584423092194},{73.9657889183145,35.73191006924026},{74.0074097183533,36.81735596177168},{73.41247541410848,34.27314856695011},{73.9156256353017,36.83206791547127},{74.81499205809087,37.15682749846019},{74.03144880081527,37.57399178552441},{74.51870941207744,38.674258946906775},{74.50754595105536,35.58903978415765},{74.51322752749547,36.030572259100154},{59.27900996617973,46.41091720294207},{59.73744793841615,46.20015558367595},{58.81134076672606,45.71150126331486},{58.52225539437495,47.416083617601544},{58.218626647023484,47.36228902172297},{60.27139669447206,46.606106348801404},{60.894962462363765,46.976924697402865},{62.29048673878424,47.66970563563518},{61.03857608977705,46.212924720020965}}, 2.0, 5, Method->"DBSCAN", DistanceFunction->EuclideanDistance)
```

### Related terms 
[BinaryDistance](BinaryDistance.md), [BrayCurtisDistance](BrayCurtisDistance.md), [ChessboardDistance](ChessboardDistance.md), [CanberraDistance](CanberraDistance.md), [CosineDistance](CosineDistance.md), [EuclideanDistance](EuclideanDistance.md), [ManhattanDistance](ManhattanDistance.md), [SquaredEuclideanDistance](SquaredEuclideanDistance.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of FindClusters](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ClusteringFunctions.java#L439) 

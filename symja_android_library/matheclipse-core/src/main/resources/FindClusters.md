## FindClusters

```
FindClusters(list-of-data-points, k)
```
 
> Clustering algorithm based on David Arthur and Sergei Vassilvitski k-means++ algorithm.. Create `k` number of clusters to split the `list-of-data-points` into.
   
See:  
* [Wikipedia - K-means++](https://en.wikipedia.org/wiki/K-means%2B%2B)
* [Wikipedia - K-means clustering](https://en.wikipedia.org/wiki/K-means_clustering) 
 
### Examples

```
>> FindClusters({1, 2, 3, 4, 5, 6, 7, 8, 9}) 
{{1.0,2.0,3.0},{6.0,7.0,8.0,9.0},{4.0,5.0}}
```
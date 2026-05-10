## CentralFeature

```
CentralFeature(list)

CentralFeature(list-of-rules)
```

> returns the central feature of a `list` or a `list-of-rules`. 

Option `DistanceFunction` can be used to define a distance function. 
- `EuclideanDistance` is the `Automatic` default value
- `EditDistance` is the default value for list of strings
- `JaccardDissimilarity` is the default value for list of boolean values

See:
* [Wikipedia - Metric_space](https://en.wikipedia.org/wiki/Metric_space)

### Examples

```
>> CentralFeature({{0,0}, {1,1}, {10,10}})
{1,1}
```

## GeoDistance

```
GeoDistance({latitude1,longitude1}, {latitude2,longitude2})
```

> returns the geodesic distance between `{latitude1,longitude1}` and `{latitude2,longitude2}`.
 

See
* [Wikipedia - Geodesics on an ellipsoid](https://en.wikipedia.org/wiki/Geodesics_on_an_ellipsoid)
* [Wikipedia - Geographical_distance]( https://en.wikipedia.org/wiki/Geographical_distance)
 
### Examples

Calculate the distance between Oslo and Berlin in miles:

```
>> GeoDistance({59.914, 10.752}, {52.523, 13.412})
521.4298968444851[mi]
```

Calculate the distance between Oslo and Berlin in kilometers:

```
>> UnitConvert(GeoDistance({59.914, 10.752}, {52.523, 13.412}), "km") 
839.1600759072911[km]
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of GeoDistance](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/GeodesyFunctions.java#L29) 

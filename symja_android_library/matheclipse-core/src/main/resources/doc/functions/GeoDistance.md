## GeoDistance

```
GeoDistance({latitude1,longitude1}, {latitude2,longitude2})
```

> returns the rhumb line distance between `{latitude1,longitude1}` and `{latitude2,longitude2}` on the WGS84 reference ellipsoid.

A rhumb line (loxodrome) is the track of constant bearing between the two points. It is always at
least as long as the geodesic, which is the shortest connection; `FindShortestTour` uses the
geodesic instead.

`GeoDistance` is implemented in the `matheclipse-astro` module and is not available in a
`matheclipse-core` only build.

See
* [Wikipedia - Rhumb line](https://en.wikipedia.org/wiki/Rhumb_line)
* [Wikipedia - Geographical distance](https://en.wikipedia.org/wiki/Geographical_distance)

### Examples

Calculate the distance between Oslo and Berlin in meters:

```
>> GeoDistance({59.914, 10.752}, {52.523, 13.412})
Quantity(839236.2,"Meters")
```

Calculate the distance between Oslo and Berlin in kilometers:

```
>> UnitConvert(GeoDistance({59.914, 10.752}, {52.523, 13.412}), "km")
Quantity(839.2362,"Kilometers")
```

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of GeoDistance](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-astro/src/main/java/org/matheclipse/astro/builtin/AstroPositionFunctions.java)

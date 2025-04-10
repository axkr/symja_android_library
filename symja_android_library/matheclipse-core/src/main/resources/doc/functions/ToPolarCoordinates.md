## ToPolarCoordinates

```
ToPolarCoordinates({x, y})
```

> return the polar coordinates for the cartesian coordinates `{x, y}`.

```
ToPolarCoordinates({x, y, z})
```
 
> return the polar coordinates for the cartesian coordinates `{x, y, z}`.


See
* [Wikipedia - Polar coordinate system](https://en.wikipedia.org/wiki/Polar_coordinate_system)
* [Wikipedia - Cartesian coordinate system](https://en.wikipedia.org/wiki/Cartesian_coordinate_system)

### Examples

```
>> ToPolarCoordinates({x, y})
{Sqrt(x^2+y^2),ArcTan(x,y)}

>> ToPolarCoordinates({x, y, z})
{Sqrt(x^2+y^2+z^2),ArcCos(x/Sqrt(x^2+y^2+z^2)),ArcTan(y,z)}
```


### Related terms 
[FromPolarCoordinates](FromPolarCoordinates.md)
  






### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of ToPolarCoordinates](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/LinearAlgebra.java#L5638) 

## ToSphericalCoordinates

```
ToSphericalCoordinates({x, y, z})
```
 
> returns the spherical coordinates for the cartesian coordinates `{x, y, z}`.


See
* [Wikipedia - Spherical coordinate system](https://en.wikipedia.org/wiki/Spherical_coordinate_system)
* [Wikipedia - Polar coordinate system](https://en.wikipedia.org/wiki/Polar_coordinate_system)
* [Wikipedia - Cartesian coordinate system](https://en.wikipedia.org/wiki/Cartesian_coordinate_system)

### Examples

```
>> ToSphericalCoordinates({x, y, z}) 
{Sqrt(x^2+y^2+z^2),ArcTan(z,Sqrt(x^2+y^2)),ArcTan(x,y)}
```
 






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of ToSphericalCoordinates](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/LinearAlgebra.java#L5250) 

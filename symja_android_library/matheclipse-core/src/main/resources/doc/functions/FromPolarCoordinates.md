## FromPolarCoordinates

```
FromPolarCoordinates({r, t})
```

> return the cartesian coordinates for the polar coordinates `{r, t}`.

```
FromPolarCoordinates({r, t, p})
```
 
> return the cartesian coordinates for the polar coordinates `{r, t, p}`.

See
* [Wikipedia - Polar coordinate system](https://en.wikipedia.org/wiki/Polar_coordinate_system)
* [Wikipedia - Cartesian coordinate system](https://en.wikipedia.org/wiki/Cartesian_coordinate_system)

### Examples

```
>> FromPolarCoordinates({r, t})
{r*Cos(t),r*Sin(t)}

>> FromPolarCoordinates({r, t, p})
{r*Cos(t),r*Cos(p)*Sin(t),r*Sin(p)*Sin(t)}
```
  
### Related terms 
[ToPolarCoordinates](ToPolarCoordinates.md)
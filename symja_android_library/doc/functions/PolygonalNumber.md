## PolygonalNumber

```
PolygonalNumber(nPoints)
```

> returns the triangular number for `nPoints`.

```
PolygonalNumber(rSides, nPoints)
```

> returns the polygonal number for `nPoints` and `rSides`.
 
See:
* [Wikipedia - Polygonal number](https://en.wikipedia.org/wiki/Polygonal_number)

### Examples

```
>> Table(PolygonalNumber(r, 3), {r, 1, 5}) 
{0,3,6,9,12}

>> PolygonalNumber(4,-2) 
4

>> PolygonalNumber(-3,-2)
-17
```


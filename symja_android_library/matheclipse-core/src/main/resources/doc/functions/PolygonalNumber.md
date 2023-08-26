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







### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of PolygonalNumber](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Combinatoric.java#L2245) 

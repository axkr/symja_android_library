## AngleVector

```
AngleVector(phi)
```

> returns the point at angle `phi` on the unit circle.

```
AngleVector({r, phi})
```

> returns the point at angle `phi` on a circle of radius `r`.

```
AngleVector({x, y}, phi)
```

> returns the point at angle `phi` on a circle of radius 1 centered at `{x, y}`.

```
AngleVector({x, y}, {r, phi})
```

> returns point at angle `phi` on a circle of radius `r` centered at `{x, y}`. 

### Examples

``` 
>> AngleVector(90*Degree)
{0,1}

>> AngleVector({1, 10}, a)
{1+Cos(a),10+Sin(a)}
```
 






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of AngleVector](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ExpTrigsFunctions.java#L156) 

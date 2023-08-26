## CollinearPoints

```
CollinearPoints({{x1,y1},{x2,y2},{a,b},...})
```

> returns true if the point `{a,b]` is on the line defined by the first two points `{x1,y1},{x2,y2}`.

```
CollinearPoints({{x1,y1,z1},{x2,y2,z2},{a,b,c},...})
```

> returns true if the point `{a,b,c]` is on the line defined by the first two points `{x1,y1,z1},{x2,y2,z2}`.

See:
* [Wikipedia - Collinearity](https://en.wikipedia.org/wiki/Collinearity)
* [Youtube - Collinear Points in 3D (Ch1 Pr18)](https://youtu.be/UDt9M8_zxlw)

### Examples


``` 
>> CollinearPoints({{1,2,3}, {3,8,1}, {7,20,-3}}) 
True
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of CollinearPoints](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ComputationalGeometryFunctions.java#L916) 

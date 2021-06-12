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

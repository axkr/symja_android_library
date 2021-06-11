## CoplanarPoints
```
CoplanarPoints({{x1,y1,z1},{x2,y2,z2},{x3,y3,z3},{a,b,c},...})
```

> returns true if the point `{a,b,c]` is on the plane defined by the first three points `{x1,y1,z1},{x2,y2,z2},{x3,y3,z3}`.

See:
* [Wikipedia - Coplanarity](https://en.wikipedia.org/wiki/Coplanarity)

### Examples

``` 
>> CoplanarPoints( {{3,2,-5}, {-1,4,-3}, {-3,8,-5}, {-3,2,1}})
True

>> CoplanarPoints( {{0,-1,-1}, {4,5,1}, {3,9,4}, {-4,4,3}}) 
False
```

## CoordinateBoundingBox

```
CoordinateBoundingBox({{x1,y1,...},{x2,y2,...},{x3,y3,...},...})
```

> calculate the bounding box of the points `{{x1,y1,...},{x2,y2,...},{x3,y3,...},...}`.
 
```
CoordinateBoundingBox({{x1,y1,...},{x2,y2,...},{x3,y3,...},...}, pad)
```

> add a pad to the calculated bounding box of the points `{{x1,y1,...},{x2,y2,...},{x3,y3,...},...}`.
 

### Examples

```
>> CoordinateBoundingBox({{0, 1}, {2, 3}, {3,4}, {2, 3}, {1,1}})
{{0,1},{3,4}}
        
>> CoordinateBoundingBox({{a,b,u}, {c,d,v}, {e,f,w}},Scaled(1/4))
{{Min(a,c,e)+1/4*(-Max(a,c,e)+Min(a,c,e)),Min(b,d,f)+1/4*(-Max(b,d,f)+Min(b,d,f)),Min(u,v,w)+
1/4*(-Max(u,v,w)+Min(u,v,w))},{Max(a,c,e)+1/4*(Max(a,c,e)-Min(a,c,e)),Max(b,d,f)+
1/4*(Max(b,d,f)-Min(b,d,f)),Max(u,v,w)+1/4*(Max(u,v,w)-Min(u,v,w))}}
            
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of CoordinateBoundingBox](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ComputationalGeometryFunctions.java#L634) 

## ArcLength

```
ArcLength(geometric-form)
```

> returns the length of the `geometric-form`.
  

See:
* [Wikipedia - Arc length](https://en.wikipedia.org/wiki/Arc_length)
 

### Examples

```
>> ArcLength(Line({{a,b},{c,d},{e,f}}))
Sqrt((a-c)^2+(b-d)^2)+Sqrt((c-e)^2+(d-f)^2)
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of ArcLength](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ComputationalGeometryFunctions.java#L182) 

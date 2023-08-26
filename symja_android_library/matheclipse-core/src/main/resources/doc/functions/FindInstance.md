## FindInstance 

```
FindInstance(equations, vars)
```

> attempts to find one solution which solves the `equations` for the variables `vars`.

### Examples

```
>> FindInstance({x^2==4,x+y^2==6}, {x,y})
{{x->-2,y->-2*Sqrt(2)}}
```

### Related terms 
[Solve](Solve.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of FindInstance](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/FindInstance.java#L45) 

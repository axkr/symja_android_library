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

### Option

```
FindInstance(..., Method -> "ORTools")
```

> searches the instances over the `Integers` with the CP-SAT solver of OR-Tools in place of choco-solver. It needs the module `matheclipse-or-tools`; without it the default method is used.

### Related terms 
[Solve](Solve.md)






### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of FindInstance](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/FindInstance.java#L44) 

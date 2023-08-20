## Rule

```
Rule(x, y)

x -> y
```

> represents a rule replacing `x` with `y`.

### Examples

``` 
>> a+b+c /. c->d
a+b+d

>> {x,x^2,y} /. x->3
{3,9,y}
``` 

Rule called with 3 arguments; 2 arguments are expected.

```
>> a /. Rule(1, 2, 3) -> t 
a
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Rule](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/PatternMatching.java#L1616) 

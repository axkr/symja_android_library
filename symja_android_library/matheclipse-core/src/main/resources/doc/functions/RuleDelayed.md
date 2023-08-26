## RuleDelayed

```
RuleDelayed(x, y)

x :> y
```

> represents a rule replacing `x` with `y`, with `y` held unevaluated. 


### Examples

```
>> Cases({1, f(2), f(3, 3, 3), 4, f(5, 5)}, f(x__) :> Plus(x))
{2,9,10}

>> Cases({1, f(2), f(3, 3, 3), 4, f(5, 5)}, f(x__) -> Plus(x))
{2,3,3,3,5,5}
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of RuleDelayed](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/PatternMatching.java#L1665) 

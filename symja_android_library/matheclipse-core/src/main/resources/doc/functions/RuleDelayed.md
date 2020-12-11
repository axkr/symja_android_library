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

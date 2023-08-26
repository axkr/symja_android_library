## TagSet

```
TagSet(f, expr, value)
```

or

```
f /: expr = value
```

> assigns the evaluated `value` to `expr` and associates the corresponding rule with the symbol `f`.


### Examples


Create an upvalue without using `UpSet`:

```
>> x /: f(x) = 2
2

>> f(x)
2

>> DownValues(f)
{}

>> UpValues(x)
{HoldPattern(f(x)):>2}
```

The symbol `f` must appear as the ultimate head of `lhs` or as the head of a leaf in `lhs`:

```
>> x /: f(g(x)) = 3
 : Tag x not found or too deep for an assigned rule.
 
>> g /: f(g(x)) = 3;
    
>> f(g(x))
3
```


### Related terms 
[Set](Set.md), [SetDelayed](SetDelayed.md), [TagSetDelayed](TagSetDelayed.md) 






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of TagSet](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/PatternMatching.java#L2157) 

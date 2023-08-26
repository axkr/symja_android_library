## GroupBy

```
GroupBy(list, head) 
```

> return an association where the elements of `list` are grouped by `head(element)`
  
```
GroupBy(assoc, head) 
```

> return an association where the rules of `assoc` are grouped by `head(rule-value)`

### Examples 

A hierachical `GroupBy`

``` 
>> expr = {{a}, {a, b}, {a, c}, {a, b, c, d}, {a, b, c, f}, {b, c}, {b, d}}; 

>> hg = Normal @ GroupBy(# /. {} -> Nothing , First -> Rest,  Function(x, hg(x, #2))) /. {Rule(a_, {b_}) :> Rule(a, b), Rule(a_, {}) :> #2(a)} &; 
				 
>> hg(expr, func) 
{a->{b->c->{func(d),func(f)},func(c)},b->{func(c),func(d)}}
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of GroupBy](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ListFunctions.java#L3462) 

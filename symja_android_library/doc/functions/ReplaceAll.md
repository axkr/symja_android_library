## ReplaceAll

```
ReplaceAll(expr, i -> new)
```

or

```
expr /. i -> new
```

> replaces all `i` in `expr` with `new`.

```
ReplaceAll(expr, {i1 -> new1, i2 -> new2, ... } )
```

or

```
expr /. {i1 -> new1, i2 -> new2, ... }
```

> replaces all `i`s in `expr` with `new`s.
 
## Examples

```
>> f(a) + f(b) /. f(x_) -> x^2
a^2+b^2
```


### Related terms 
[Dispatch](Dispatch.md), [Replace](Replace.md), [ReplaceList](ReplaceList.md), [ReplacePart](ReplacePart.md), [ReplaceRepeated](ReplaceRepeated.md)







### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of ReplaceAll](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ListFunctions.java#L5819) 

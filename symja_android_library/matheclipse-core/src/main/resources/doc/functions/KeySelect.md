## KeySelect

```
KeySelect(<|key1->value1, ...|>, head)
```

> returns an association of the elements  for which `head(keyi)` returns `True`.

```
KeySelect({key1->value1, ...}, head)
```

> returns an association of the elements  for which `head(keyi)` returns `True`.

### Examples

```
>> r = {beta -> 4, alpha -> 2, x -> 4, z -> 2, w -> 0.8}; 

>> KeySelect(r, MatchQ(#,alpha|x)&) 
<|alpha->2,x->4|>
```







### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of KeySelect](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/AssociationFunctions.java#L728) 

## Merge

```
Merge(list-of-rules-or-associations, function)
```

> use the `function` to merge right-hand-side values with the left-hand-side key in the `list-of-rules-or-associations`.

### Examples

```
>> Merge({<|x->1|>, x->2, x->3, {y->4}}, Identity) 
<|x->{1,2,3},y->{4}|>
```

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Merge](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/AssociationFunctions.java#L1022) 

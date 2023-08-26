## Unset

```
Unset(expr)
```

or

```
expr =.
```

> removes any definitions belonging to the left-hand-side `expr`.

### Examples
 
```
>> a = 2
2

>> a =.

>> a
a
```

Unsetting an already unset or never defined variable will not change anything:

```
>> a =.

>> b =.
```

`Unset` can unset particular function values. It will print a message if no corresponding rule is found.

```
>> f[x_) =.
Assignment not found for: f(x_)

>> f(x_) := x ^ 2

>> f(3)
9

>> f(x_) =.

>> f(3)
f(3)
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Unset](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/PatternMatching.java#L2461) 

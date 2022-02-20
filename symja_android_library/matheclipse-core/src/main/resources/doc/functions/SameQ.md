## SameQ

```
SameQ(x, y)

x===y
```

> returns `True` if `x` and `y` are structurally identical.

See
* [Wikipedia - Computer algebra - Equality](https://en.wikipedia.org/wiki/Computer_algebra#Equality)

### Examples

Any object is the same as itself:

```
>> a===a
True
```

Unlike `Equal`, `SameQ` only yields `True` if `x` and `y` have the same type:

```
>> {1==1., 1===1.}
{True,False}
```


### Related terms
[Equal](Equal.md), [Unequal](Unequal.md), [UnsameQ](UnsameQ.md) 

### Github

* [Implementation of SameQ](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/BooleanFunctions.java#L3600) 

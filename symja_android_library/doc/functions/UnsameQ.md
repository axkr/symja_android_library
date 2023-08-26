## UnsameQ

```
UnsameQ(x, y)

x=!=y
```

> returns `True` if `x` and `y` are not structurally identical.

See
* [Wikipedia - Computer algebra - Equality](https://en.wikipedia.org/wiki/Computer_algebra#Equality)

### Examples

Any object is the same as itself:

```
>> a=!=a
False

>> 1=!=1.0
True
```


### Related terms
[Equal](Equal.md), [SameQ](SameQ.md) , [Unequal](Unequal.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of UnsameQ](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/BooleanFunctions.java#L4717) 

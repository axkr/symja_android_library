## UnsameQ

```
UnsameQ(x, y)

x=!=y
```

> returns `True` if `x` and `y` are not structurally identical.

### Examples

Any object is the same as itself:

```
>> a=!=a
False

>> 1=!=1.0
True
```


### Github

* [Implementation of UnsameQ](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/BooleanFunctions.java#L4199) 

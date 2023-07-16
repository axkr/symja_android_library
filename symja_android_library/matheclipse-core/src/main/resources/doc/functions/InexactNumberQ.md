## InexactNumberQ

```
InexactNumberQ(expr)
```

> returns `True` if `expr` is not an exact number, and `False` otherwise.

### Examples

```
>> InexactNumberQ(a)
False
 
>> InexactNumberQ(3.0)
True
 
>> InexactNumberQ(2/3)
False
```

`InexactNumberQ` can be applied to complex numbers:

```
>> InexactNumberQ(4.0+I)    
True
```


### Github

* [Implementation of InexactNumberQ](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/expression/BuiltInSymbol.java#L2571) 

## BooleanQ

```
BooleanQ(expr) 
```

> returns `True` if `expr` is either `True` or `False`.

### Examples
 
```
>> BooleanQ(True)
True
>> BooleanQ(False)
True
>> BooleanQ(a)
False
>> BooleanQ(1 < 2)
True
>> BooleanQ("string")
False
>> BooleanQ(Together(x/y + y/x))
False
```

### Github

* [Implementation of BooleanQ](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/expression/BuiltInSymbol.java#L1307) 

## ListQ

```
ListQ(expr)
```

> tests whether `expr` is a `List`.

### Examples

```
>> ListQ({1, 2, 3})
True

>> ListQ({{1, 2}, {3, 4}})
True

>> ListQ(x)
False
```

### Github

* [Implementation of ListQ](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/expression/BuiltInSymbol.java#L13) 

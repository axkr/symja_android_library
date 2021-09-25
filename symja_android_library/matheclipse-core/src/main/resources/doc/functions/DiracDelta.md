## DiracDelta

```
DiracDelta(x)
```

> `DiracDelta` function returns `0` for all real numbers `x` where `x != 0`.
 

### Examples
```
>> DiracDelta(-42)
0
```

`DiracDelta` doesn't evaluate for `0`:

```
>> DiracDelta(0)
DiracDelta(0)
```

### Github

* [Implementation of DiracDelta](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/NumberTheory.java#L1300) 

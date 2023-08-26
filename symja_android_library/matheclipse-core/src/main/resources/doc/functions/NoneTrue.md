## NoneTrue

```
NoneTrue({expr1, expr2, ...}, test)
```

> returns `True` if no application of `test` to `expr1, expr2, ...` evaluates to `True`.

```
NoneTrue(list, test, level)
```

> returns `True` if no application of `test` to items of `list` at `level` evaluates to `True`.

```
NoneTrue(test)
```

> gives an operator that may be applied to expressions.

### Examples

```
>> NoneTrue({1, 3, 5}, EvenQ)
True

>> NoneTrue({1, 4, 5}, EvenQ)
False

>> NoneTrue({}, EvenQ)
True
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of NoneTrue](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/BooleanFunctions.java#L3554) 

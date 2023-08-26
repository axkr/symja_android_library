## AllTrue

``` 
AllTrue({expr1, expr2, ...}, test)
```

> returns `True` if all applications of `test` to `expr1, expr2, ...` evaluate to `True`.

```
AllTrue(list, test, level)
```

> returns `True` if all applications of `test` to items of `list` at `level` evaluate to `True`.

```
AllTrue(test)
```

> gives an operator that may be applied to expressions.

### Examples
```
>> AllTrue({2, 4, 6}, EvenQ)
True
>> AllTrue({2, 4, 7}, EvenQ)
False
>> AllTrue({}, EvenQ)
True
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of AllTrue](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/BooleanFunctions.java#L592) 

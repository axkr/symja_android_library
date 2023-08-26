## AnyTrue

```
AnyTrue({expr1, expr2, ...}, test)
```

> returns `True` if any application of `test` to `expr1, expr2, ...` evaluates to `True`.

```
AnyTrue(list, test, level)
```

> returns `True` if any application of `test` to items of `list` at `level` evaluates to `True`.

```
AnyTrue(test)
```
> gives an operator that may be applied to expressions.

### Examples

```
>> AnyTrue({1, 3, 5}, EvenQ)
False
>> AnyTrue({1, 4, 5}, EvenQ)
True
>> AnyTrue({}, EvenQ)
False
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of AnyTrue](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/BooleanFunctions.java#L844) 

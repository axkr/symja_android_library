## NestWhileList

```
NestWhileList(f, expr, test)
```

> applies a function `f` repeatedly on an expression `expr`, until applying `test` on the result no longer yields `True`. It returns a list of all intermediate results.

```
NestWhileList(f, expr, test, m)
```

> supplies the last `m` results to `test` (default value: `1`). It returns a list of all intermediate results.
	
```
NestWhileList(f, expr, test, All)
```

> supplies all results gained so far to `test`. It returns a list of all intermediate results.

```
NestWhileList[f, expr, test, m, max, n]
```

> applies `f` to `expr` until `test` does not return `True`. It returns a list of all intermediate results. `test` is a function that takes as its arguments the last `m` results. `max` denotes the maximum number of applications of `f` and `n` denotes that `f` should be applied another `n` times after `test` has terminated the recursion. If `n` is a negative integer, the last `-n` elements will be dropped.

### Examples

Divide by 2 until the result is no longer an integer:

``` 
>> NestWhileList(#/2&, 10000, IntegerQ) 
{10000,5000,2500,1250,625,625/2}

>> NestWhileList(#+1 &, 1, True &, 1, 4, 5)
{1,2,3,4,5,6,7,8,9,10}
```







### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of NestWhileList](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Programming.java#L1760) 

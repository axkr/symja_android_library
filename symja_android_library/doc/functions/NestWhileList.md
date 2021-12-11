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

### Examples

Divide by 2 until the result is no longer an integer:

``` 
>> NestWhileList(#/2&, 10000, IntegerQ) 
{10000,5000,2500,1250,625,625/2}
```



### Github

* [Implementation of NestWhileList](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Programming.java#L1681) 

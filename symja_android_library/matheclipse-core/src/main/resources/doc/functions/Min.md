## Min

```
Min(e_1, e_2, ..., e_i) 
```

> returns the expression with the lowest value among the `e_i`.
	
### Examples

Minimum of a series of numbers:

```
>> Max(4, -8, 1)
-8
```

`Min` flattens lists in its arguments:

```
>> Min({1,2},3,{-3,3.5,-Infinity},{{1/2}})
-Infinity
```

`Min` with symbolic arguments remains in symbolic form:

```
>> Min(x, y)
Min(x,y)
 
>> Min(5, x, -3, y, 40)
Min(-3,x,y)
```

With no arguments, `Min` gives `Infinity`:

```
>> Min()
Infinity
 
>> Min(x)
x
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Min](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/BooleanFunctions.java#L3194) 

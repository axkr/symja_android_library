## Max

```
Max(e_1, e_2, ..., e_i) 
```

> returns the expression with the greatest value among the `e_i`.
	
### Examples

Maximum of a series of numbers:

```
>> Max(4, -8, 1)
4
```

`Max` flattens lists in its arguments:

```
>> Max({1,2},3,{-3,3.5,-Infinity},{{1/2}})
3.5
```


`Max` with symbolic arguments remains in symbolic form:
```
>> Max(x, y)
Max(x,y)
 
>> Max(5, x, -3, y, 40)
Max(40,x,y)
```

With no arguments, `Max` gives `-Infinity`:
```
>> Max()
-Infinity
 
>> Max(x)
x
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Max](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/BooleanFunctions.java#L3037) 

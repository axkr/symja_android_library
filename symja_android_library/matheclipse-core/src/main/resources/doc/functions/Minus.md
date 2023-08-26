## Minus

```
Minus(expr)

-expr
```

> is the negation of `expr`. 
 

### Examples

```
>> -a //FullForm
"Times(-1, a)"
```
 
`Minus` automatically distributes:

```
>> -(x - 2/3)
2/3-x
```

`Minus` threads over lists:

```
>> -Range(10)
{-1,-2,-3,-4,-5,-6,-7,-8,-9,-10}
```
	






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Minus](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Arithmetic.java#L2444) 

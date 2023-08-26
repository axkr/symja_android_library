## Negative

```
Negative(x)  
```

> returns `True` if `x` is a negative real number.
	
### Examples
 
```
>> Negative(0)
False

>> Negative(-3)
True

>> Negative(10/7)
False

>> Negative(1+2*I)
False

>> Negative(a + b)
Negative(a+b)

>> Negative(-E)
True

>> Negative(Sin({11, 14}))
{True, False}
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Negative](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/BooleanFunctions.java#L3469) 

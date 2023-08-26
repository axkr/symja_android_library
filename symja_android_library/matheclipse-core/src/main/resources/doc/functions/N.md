## N

```
N(expr)
```

> gives the numerical value of `expr`.  


```
N(expr, precision)
```

> evaluates `expr` numerically with a precision of `prec` digits.  


**Note**: the upper case identifier `N` is different from the lower case identifier `n`.
 
### Examples 

``` 
>> N(Pi)
3.141592653589793

>> N(Pi, 50)
3.1415926535897932384626433832795028841971693993751

>> N(1/7)
0.14285714285714285

>> N(1/7, 5)
1.4285714285714285714e-1
```
 
### Related terms 
[EvalF](EvalF.md)
 






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of N](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Arithmetic.java#L2508) 

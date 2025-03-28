## Range

```
Range(n)
```

> returns a list of integers from `1` to `n`.
	
```
Range(a, b)
```

> returns a list of integers from `a` to `b`.

```
Range[a, b, di]
```

> returns a list of values from `a` to `b` using step $di$. More specifically, 'Range' starts from `a` and successively adds increments of `di` until the result is greater `(if di > 0)` or less `(if di < 0)` than `b`.
        
### Examples

```
>> Range(5)
{1,2,3,4,5}

>> Range(-3, 2)
{-3,-2,-1,0,1,2} 

>> Range(0, 2, 1/3)
{0,1/3,2/3,1,4/3,5/3,2}

>> x^Range(n, n + 10, 2)
{x^n,x^(2+n),x^(4+n),x^(6+n),x^(8+n),x^(10+n)}

>> Range(a, a + 12*n, 2*n)
{a,a+2*n,a+4*n,a+6*n,a+8*n,a+10*n,a+12*n}

>> a + Range(0, 3, Pi/8)
{a,a+Pi/8,a+Pi/4,a+3/8*Pi,a+Pi/2,a+5/8*Pi,a+3/4*Pi,a+7/8*Pi}

>> x * Range(-1, 1, 1/5)
{-x,-4/5*x,-3/5*x,-2/5*x,-x/5,0,x/5,2/5*x,3/5*x,4/5*x,x}
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Range](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ListFunctions.java#L5532) 

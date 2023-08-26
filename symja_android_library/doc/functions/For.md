## For

```
For(start, test, incr, body)
```
> evaluates `start`, and then iteratively `body` and `incr` as long as test evaluates to `True`.

```
For(start, test, incr)
```
> evaluates only `incr` and no `body`.

```
For(start, test)
```
> runs the loop without any body.  
  
See:  
* [Wikipedia - For loop](https://en.wikipedia.org/wiki/For_loop)
  
### Examples

Compute the factorial of 10 using `For`:

``` 
>> n := 1
>> For(i=1, i<=10, i=i+1, n = n * i)
>> n
3628800
 
>> n == 10!
True
 
>> n := 1
>> For(i=1, i<=10, i=i+1, If(i > 5, Return(i)); n = n * i)
6
 
>> n
120
```

### Related terms 
[Break](Break.md), [Continue](Continue.md), [Do](Do.md), [While](While.md) 






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of For](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Programming.java#L1125) 

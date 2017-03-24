## FixedPoint  

``` 
FixedPoint(f, expr)
```  
 
> starting with `expr`, repeatedly applies `f` until the result no longer changes.

``` 
FixedPoint(f, expr, n)
``` 

> performs at most `n` iterations.

### Examples  
``` 
>>> FixedPoint(Cos, 1.0)
0.7390851332151607
``` 


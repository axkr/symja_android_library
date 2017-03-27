## Array 

``` 
Array(expr, length)
``` 

> computes a list of elements from `expr(1)` to `expr(length)`


### Examples
``` 
>>> Array(f, 4)
{f(1),f(2),f(3),f(4)}
```  

``` 
>>> Array(f, {2, 3})
{{f(1,1),f(1,2),f(1,3)},{f(2,1),f(2,2),f(2,3)}}
```  

``` 
>>> Array(f, {2, 3}, {4, 6})
{{f(4,6),f(4,7),f(4,8)},{f(5,6),f(5,7),f(5,8)}}
``` 
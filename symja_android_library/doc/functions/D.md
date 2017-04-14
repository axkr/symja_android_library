## D

``` 
D(f, x)
``` 
> gives the partial derivative of `f` with respect to `x`. 

``` 
D(f, {x,n})
```  
> gives the `n`th derivative of `f` with respect to `x`.  

**Note**: the upper case identifier `D` is different from the lower case identifier `d`.
 
### Examples 
 
``` 
>> D(Sin(x),x)
Cos(x)
``` 

``` 
>> D(x^5,{x,2})
20*x^3
``` 
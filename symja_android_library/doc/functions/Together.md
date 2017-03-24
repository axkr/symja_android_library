## Together

``` 
Together(expr)
``` 

> puts terms in a sum over a common denominator and cancels factors in the result. 

### Examples 
``` 
>>> Together(a/b+x/y)
(a*y+b*x)*b^(-1)*y^(-1)
``` 
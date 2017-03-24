## Apart

```
Apart(expr)
```

> rewrites `expr` as sum of individual fractions. 

```
Apart(expr, var)
``` 

> treats `var` as main variable. 

### Examples

``` 
>>> Apart((x-1)/(x^2+x))
2/(x+1)-1/x
```

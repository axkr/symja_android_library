## JacobiMatrix

```
JacobiMatrix(matrix, var)
```

> creates a Jacobian matrix.


### Examples 

```
>> JacobiMatrix({f(u),f(v),f(w),f(x)}, {u,v,w})
{{f'(u),0,0},{0,f'(v),0},{0,0,f'(w)},{0,0,0}}
```